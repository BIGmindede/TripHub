package triphub.trip.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import triphub.trip.models.Participation;

import java.util.UUID;

public interface ParticipationRepository extends ReactiveCrudRepository<Participation, UUID> {

    Flux<Participation> findByProfileId(UUID profileId);

    Flux<Participation> findByTripId(UUID tripId);
}