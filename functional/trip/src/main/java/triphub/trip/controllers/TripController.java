package triphub.trip.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triphub.trip.DTOs.TripAndParticipationsDTO;
import triphub.trip.DTOs.TripAndParticipationsDTO.ParticipationAndNotificationInfo;
import triphub.trip.DTOs.TripStatisticsDTO;
import triphub.trip.models.Participation;
import triphub.trip.models.Trip;
import triphub.trip.models.constants.ParticipationStatuses;
import triphub.trip.services.ParticipationService;
import triphub.trip.services.TripService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final ParticipationService participationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Trip> createTrip(
        @RequestBody TripAndParticipationsDTO tripAndParticipationsDTO,
        ServerWebExchange exchange
    ) {
        return tripService.createTrip(
                tripAndParticipationsDTO.getTrip(),
                tripAndParticipationsDTO
                    .getParticipationAndNotificationInfo()
                    .participations()
                    .size()
            )
            .flatMap(createdTrip -> 
                participationService.createParticipations(
                    createdTrip.getAuthorId(),
                    tripAndParticipationsDTO.getParticipationAndNotificationInfo(),
                    createdTrip.getId()
                )
                .thenReturn(createdTrip) // Ждем завершения createParticipations
            );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Trip>> getTripById(@PathVariable UUID id) {
        return tripService.getTripById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/current")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<Trip>> getCurrentTrip(
        @CookieValue("profile_id") String profileId,
        ServerWebExchange exchange
    ) {
        return tripService.getCurrentTrip(UUID.fromString(profileId))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/by_profile")
    public Flux<Trip> getTripsByProfileId(@CookieValue("profile_id") String profileId) {
        return tripService.getTripsByProfileId(UUID.fromString(profileId));
    }

    @GetMapping("/statistics")
    public Mono<TripStatisticsDTO> getTripsStatisticsByProfileId(@CookieValue("profile_id") String profileId) {
        return tripService.getTripsStatisticsByProfileId(UUID.fromString(profileId));
    }

    @GetMapping("/by_destination/{destination}")
    public Flux<Trip> getTripsByDestination(@PathVariable String destination) {
        return tripService.getTripsByDestination(destination);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Trip>> updateTrip(@PathVariable UUID id, @RequestBody Trip trip) {
        return tripService.updateTrip(id, trip)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrip(@PathVariable UUID id) {
        return tripService.deleteTrip(id)
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }

    // Participation endpoints
    @PostMapping("/{tripId}/participations")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Void>> createParticipations(@PathVariable UUID tripId, @RequestBody ParticipationAndNotificationInfo participationAndNotificationInfo) {
        return participationService.createParticipations(null, participationAndNotificationInfo, tripId)
                .map(Void -> ResponseEntity.noContent().<Void>build());
    }

    @GetMapping("/{tripId}/participations")
    public Flux<Participation> getParticipationsByTripId(@PathVariable UUID tripId) {
        return participationService.getParticipationsByTripId(tripId);
    }

    @PutMapping("/participations/setover")
    public Mono<ResponseEntity<Void>> setOverParticipations(
        @RequestBody List<UUID> participationIds
    ) {
        return participationService.setOverParticipations(participationIds)
            .map(Void -> ResponseEntity.noContent().<Void>build());
    }

    @PutMapping("/participations/accept/{participationId}")
    public Mono<ResponseEntity<Void>> acceptParticipationInvitation(
        @PathVariable UUID participationId
    ) {
        return participationService.updateParticipation(participationId, ParticipationStatuses.ACCEPTED)
            .map(Void -> ResponseEntity.noContent().<Void>build());
    }

    @PutMapping("/participations/decline/{participationId}")
    public Mono<ResponseEntity<Void>> declineParticipationInvitation(
        @PathVariable UUID participationId
    ) {
        return participationService.deleteParticipations(List.of(participationId))
            .thenReturn(ResponseEntity.noContent().<Void>build());
    }

    @DeleteMapping("/participations/{participationId}")
    public Mono<ResponseEntity<Void>> deleteParticipations(@PathVariable UUID participationId) {
        return participationService.deleteParticipations(List.of(participationId))
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }
}