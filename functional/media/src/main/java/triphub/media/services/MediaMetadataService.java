package triphub.media.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.minio.BucketExistsArgs;
import io.minio.ComposeObjectArgs;
import io.minio.ComposeSource;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.SetBucketPolicyArgs;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triphub.media.DTOs.ChunkUploadRequest;
import triphub.media.DTOs.MediaMetadataDto;
import triphub.media.DTOs.UploadResponse;
import triphub.media.exceptions.MediaServiceException;
import triphub.media.models.MediaMetadata;
import triphub.media.repositories.MediaMetadataRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaMetadataService {
    private final MinioClient minioClient;
    private final String bucketName;
    private final MediaMetadataRepository metadataRepository;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void initBucket() {
        try {
            boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!exists) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                String publicReadPolicy = """
                {
                    "Version": "2012-10-17",
                    "Statement": [
                        {
                            "Effect": "Allow",
                            "Principal": "*",
                            "Action": ["s3:GetObject"],
                            "Resource": ["arn:aws:s3:::%s/*"]
                        }
                    ]
                }
                """.formatted(bucketName);
                minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                        .bucket(bucketName)
                        .config(publicReadPolicy)
                        .build());

                log.info("Bucket {} created with public read access", bucketName);
            } else {
                log.info("Bucket {} already exists", bucketName);
            }
        } catch (Exception e) {
            log.error("Bucket initialization failed", e);
            throw new IllegalStateException("Failed to initialize MinIO bucket", e);
        }
    }

    public Mono<MediaMetadata> uploadFullFile(FilePart filePart, String metadataJson) {
        try {
            MediaMetadataDto dto = objectMapper.readValue(metadataJson, MediaMetadataDto.class);
            String objectName = UUID.randomUUID() + "/" + filePart.filename();
    
            Path tempFile = Files.createTempFile("upload-", ".tmp");
            
            return filePart.transferTo(tempFile)
                .then(Mono.fromCallable(() -> {
                    try (InputStream is = Files.newInputStream(tempFile)) {
                        minioClient.putObject(
                            PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(is, Files.size(tempFile), -1)
                                .contentType(filePart.headers().getContentType().toString())
                                .build());
                    }
                    return objectName;
                }))
                .flatMap(name -> saveMetadata(dto, name))
                .doFinally(s -> cleanupTempFile(tempFile));
        } catch (Exception e) {
            return Mono.error(new MediaServiceException("File upload failed", e));
        }
    }

    public Mono<UploadResponse> uploadChunk(String metadataJson, FilePart filePart) {
        try {
            ChunkUploadRequest request = objectMapper.readValue(metadataJson, ChunkUploadRequest.class);
            String objectName = String.format("chunks/%s/%s/part-%d", 
                request.getUploadId(),
                request.getFileName(),
                request.getChunkNumber());

            Path tempFile = Files.createTempFile("chunk-", ".tmp");
            
            return filePart.transferTo(tempFile)
                .then(Mono.fromCallable(() -> {
                    try (InputStream is = Files.newInputStream(tempFile)) {
                        minioClient.putObject(
                            PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(is, Files.size(tempFile), -1)
                                .contentType(filePart.headers().getContentType().toString())
                                .build());
                        return new UploadResponse(
                            objectName,
                            request.getChunkNumber(),
                            request.getChunkNumber() == request.getTotalChunks() - 1
                        );
                    }
                }))
                .doFinally(s -> cleanupTempFile(tempFile));
        } catch (Exception e) {
            return Mono.error(new MediaServiceException("Chunk upload failed", e));
        }
    }

    public Mono<MediaMetadata> completeUpload(String uploadId, String fileName, MediaMetadataDto metadataDto) {
        String prefix = String.format("chunks/%s/%s/", uploadId, fileName);
        String finalObjectName = String.format("media/%s/%s", UUID.randomUUID(), fileName);
    
        return getChunkList(prefix)
            .collectList()
            .flatMap(chunks -> {
                if (chunks.isEmpty()) {
                    return Mono.error(new MediaServiceException("No chunks found for upload"));
                }
                return composeFile(chunks, finalObjectName);
            })
            .flatMap(objectName -> saveMetadata(metadataDto, objectName))
            .doOnSuccess(m -> cleanupChunks(prefix));
    }

    private Flux<String> getChunkList(String prefix) {
        return Flux.create(emitter -> {
            try {
                Iterable<Result<Item>> results = minioClient.listObjects(
                    ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(prefix)
                        .build());

                for (Result<Item> result : results) {
                    try {
                        Item item = result.get();
                        emitter.next(item.objectName());
                    } catch (Exception e) {
                        emitter.error(new MediaServiceException("Error listing chunks", e));
                        return;
                    }
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.error(new MediaServiceException("Error listing objects", e));
            }
        });
    }

    private Mono<String> composeFile(List<String> chunks, String finalObjectName) {
        return Mono.fromCallable(() -> {
            List<ComposeSource> sources = chunks.stream()
                .map(name -> ComposeSource.builder()
                    .bucket(bucketName)
                    .object(name)
                    .build())
                .collect(Collectors.toList());

            minioClient.composeObject(ComposeObjectArgs.builder()
                .bucket(bucketName)
                .object(finalObjectName)
                .sources(sources)
                .build());
            return finalObjectName;
        });
    }

    private void cleanupTempFile(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            log.error("Temp file deletion failed: {}", file, e);
        }
    }

    private void cleanupChunks(String prefix) {
        getChunkList(prefix)
            .flatMap(name -> Mono.fromCallable(() -> {
                minioClient.removeObject(
                    RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(name)
                        .build());
                return name;
            }).onErrorResume(e -> {
                log.error("Failed to delete chunk: {}", name, e);
                return Mono.empty();
            }))
            .subscribe();
    }

    private Mono<MediaMetadata> saveMetadata(MediaMetadataDto dto, String mediaUrl) {
        MediaMetadata metadata = MediaMetadata.builder()
            .authorId(dto.getAuthorId())
            .mediaUrl(mediaUrl)
            .tripId(dto.getTripId())
            .isOpenedForPublish(dto.getIsOpenedForPublish())
            .geodata(dto.getGeodata())
            .createdAt(LocalDateTime.now())
            .contentType(dto.getContentType())
            .fileSize(dto.getFileSize())
            .build();
        
        return metadataRepository.save(metadata);
    }

    public Flux<MediaMetadata> getMediaByAuthor(UUID authorId) {
        return metadataRepository.findByAuthorId(authorId);
    }
    
    public Flux<MediaMetadata> getMediaByTrip(UUID tripId) {
        return metadataRepository.findByTripId(tripId);
    }
    
    public Flux<MediaMetadata> getPublicMediaByTrip(UUID tripId) {
        return metadataRepository.findByTripIdAndIsOpenedForPublishTrue(tripId);
    }

    public Mono<Void> deleteMedia(UUID id) {
        return metadataRepository.findById(id)
            .flatMap(metadata -> {
                String objectName = metadata.getMediaUrl();
                return Mono.fromCallable(() -> {
                        minioClient.removeObject(
                            RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build());
                        return metadata;
                    })
                    .flatMap(m -> metadataRepository.deleteById(id));
            })
            .then();
    }
    
    public Mono<MediaMetadata> updatePublishFlag(UUID id, Boolean isOpenedForPublish) {
        return metadataRepository.findById(id)
            .flatMap(metadata -> {
                metadata.setIsOpenedForPublish(isOpenedForPublish);
                return metadataRepository.save(metadata);
            })
            .switchIfEmpty(Mono.error(
                new MediaServiceException("Media not found with id: " + id)));
    }
    
    public Mono<MediaMetadata> updateGeodata(UUID id, String geodata) {
        return metadataRepository.findById(id)
            .flatMap(metadata -> {
                metadata.setGeodata(geodata);
                return metadataRepository.save(metadata);
            })
            .switchIfEmpty(Mono.error(
                new MediaServiceException("Media not found with id: " + id)));
    }
}