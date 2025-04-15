package triphub.trip.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import triphub.trip.models.Trip;
import triphub.trip.models.constants.ParticipationStatuses;

import java.util.UUID;

public interface TripRepository extends ReactiveCrudRepository<Trip, UUID> {
    @Query("SELECT t.* FROM trips t JOIN participations p ON t.id = p.trip_id WHERE p.profile_id = :profileId and p.status = :participationStatus")
    Flux<Trip> findTripsByProfileId(UUID profileId, ParticipationStatuses participationStatus);

    @Query("SELECT * FROM trips WHERE destination ILIKE '%' || :destination || '%'")
    Flux<Trip> findTripsByDestinationContaining(String destination);
}