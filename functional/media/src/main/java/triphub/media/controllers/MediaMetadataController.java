package triphub.media.controllers;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triphub.media.DTOs.MediaMetadataDto;
import triphub.media.DTOs.UploadResponse;
import triphub.media.models.MediaMetadata;
import triphub.media.services.MediaMetadataService;

@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
public class MediaMetadataController {
    private final MediaMetadataService mediaService;
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<MediaMetadata> uploadFile(
        @RequestPart("file") FilePart filePart,
        @RequestPart("metadata") String metadataJson) {
        
        return mediaService.uploadFullFile(filePart, metadataJson);
    }

    @PostMapping(value = "/chunk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<UploadResponse> uploadChunk(
            @RequestPart("metadata") String metadataJson,
            @RequestPart("file") FilePart filePart) {
        return mediaService.uploadChunk(metadataJson, filePart);
    }
    
    @PostMapping("/complete")
    public Mono<MediaMetadata> completeUpload(
            @RequestParam String uploadId,
            @RequestParam String fileName,
            @RequestBody MediaMetadataDto metadataDto) {
        return mediaService.completeUpload(uploadId, fileName, metadataDto);
    }
    
    @GetMapping("/author/{authorId}")
    public Flux<MediaMetadata> getMediaByAuthor(@PathVariable UUID authorId) {
        return mediaService.getMediaByAuthor(authorId);
    }
    
    @GetMapping("/trip/{tripId}")
    public Flux<MediaMetadata> getMediaByTrip(@PathVariable UUID tripId) {
        return mediaService.getMediaByTrip(tripId);
    }

    @GetMapping("/trip/{tripId}/public")
    public Flux<MediaMetadata> getPublicMediaByTrip(@PathVariable UUID tripId) {
        return mediaService.getPublicMediaByTrip(tripId);
    }

    @PatchMapping("/{id}/publish")
    public Mono<MediaMetadata> setPublishFlag(
            @PathVariable UUID id,
            @RequestParam Boolean isOpenedForPublish) {
        return mediaService.updatePublishFlag(id, isOpenedForPublish);
    }

    @PatchMapping("/{id}/geodata")
    public Mono<MediaMetadata> updateGeodata(
            @PathVariable UUID id,
            @RequestBody String geodata) {
        return mediaService.updateGeodata(id, geodata);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteMedia(@PathVariable UUID id) {
        return mediaService.deleteMedia(id);
    }


}
