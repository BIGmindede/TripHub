package triphub.trip.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triphub.trip.models.Participation;

import java.util.List;
import java.util.UUID;

public interface ParticipationRepository extends ReactiveCrudRepository<Participation, UUID> {

    @Query("UPDATE participations SET is_current = false WHERE id IN (:ids)")
    Mono<Void> disableByIds(List<UUID> ids);

    Flux<Participation> findByProfileId(UUID profileId);

    Flux<Participation> findByTripId(UUID tripId);

    Mono<Participation> findByProfileIdAndTripId(UUID profileId, UUID tripId);

    Mono<Participation> findByProfileIdAndIsCurrent(UUID profileId, Boolean isCurrent);
}