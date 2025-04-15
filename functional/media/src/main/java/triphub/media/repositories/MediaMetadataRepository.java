package triphub.media.repositories;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import triphub.media.models.MediaMetadata;

public interface MediaMetadataRepository extends ReactiveCrudRepository<MediaMetadata, UUID> {
    Flux<MediaMetadata> findByAuthorId(UUID authorId);
    Flux<MediaMetadata> findByTripId(UUID tripId);
    Flux<MediaMetadata> findByTripIdAndIsOpenedForPublishTrue(UUID tripId);
}