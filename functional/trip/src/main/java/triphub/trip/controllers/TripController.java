package triphub.trip.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triphub.trip.DTOs.TripAndParticipationsDTO;
import triphub.trip.DTOs.TripAndParticipationsDTO.ProfilePart;
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
    public Mono<Trip> createTrip(@RequestBody TripAndParticipationsDTO tripAndParticipationsDTO) {
        return tripService.createTrip(
            tripAndParticipationsDTO.toTrip(),
            tripAndParticipationsDTO.getParticipations().size()
        ).flatMap(createdTrip ->
                participationService.createParticipations(
                    createdTrip.getAuthorId(),
                    tripAndParticipationsDTO.getParticipations(),
                    createdTrip.getId())
                            .thenReturn(createdTrip)
        );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Trip>> getTripById(@PathVariable UUID id) {
        return tripService.getTripById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/by_profileid/{id}")
    public Flux<Trip> getTripsByProfileId(@PathVariable UUID id) {
        return tripService.getTripsByProfileId(id);
    }

    @GetMapping("/by_destination/{destination}")
    public Flux<Trip> getTripsByDestination(@PathVariable String destination) {
        return tripService.getTripsByDestination(destination);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateTrip(@PathVariable UUID id, @RequestBody Trip trip) {
        return tripService.updateTrip(id, trip)
                .map(Trip -> ResponseEntity.noContent().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTrip(@PathVariable UUID id) {
        return tripService.deleteTrip(id)
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }

    // Participations
    @PostMapping("/participations/{trip_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Void>> createParticipations(@PathVariable UUID trip_id, @RequestBody List<ProfilePart> profiles) {
        return participationService.createParticipations(null, profiles, trip_id)
                .map(Void -> ResponseEntity.noContent().<Void>build());
    }

    @GetMapping("/participations/{trip_id}")
    public Flux<Participation> getParticipationsByTripId(@PathVariable UUID trip_id) {
        return participationService.getParticipationsByTripId(trip_id);
    }

    @GetMapping("/participations/accept/{participation_id}")
    public Mono<ResponseEntity<Void>> acceptParticipationInvitation(@PathVariable UUID participation_id) {
        return participationService.updateParticipation(participation_id, ParticipationStatuses.ACCEPTED)
                .map(Void -> ResponseEntity.noContent().<Void>build());
    }

    @GetMapping("/participations/decline/{participation_id}")
    public Mono<ResponseEntity<Void>> declineParticipationInvitation(@PathVariable UUID participation_id) {
        return participationService.deleteParticipations(List.of(participation_id))
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }

    @DeleteMapping("/participations")
    public Mono<ResponseEntity<Void>> deleteParticipations(@RequestBody List<UUID> participationsIds) {
        return participationService.deleteParticipations(participationsIds)
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }
}