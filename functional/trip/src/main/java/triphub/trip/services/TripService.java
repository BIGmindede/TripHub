package triphub.trip.services;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triphub.trip.mappers.TripMapper;
import triphub.trip.models.Trip;
import triphub.trip.models.constants.ParticipationStatuses;
import triphub.trip.repositories.TripRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    private final RabbitService rabbitMQService;
    

    public Mono<Trip> createTrip(Trip trip, Integer participantsAmount) {
        return tripRepository.save(trip)
            .flatMap(createdTrip -> {
                return rabbitMQService.createLinkedReport(createdTrip, participantsAmount)
                    .then(rabbitMQService.createLinkedKanban(createdTrip))
                    .thenReturn(createdTrip);
            });
    }

    public Mono<Trip> getTripById(UUID id) {
        return tripRepository.findById(id);
    }

    public Flux<Trip> getTripsByProfileId(UUID id) {
        return tripRepository.findTripsByProfileId(id, ParticipationStatuses.ACCEPTED);
    }

    public Flux<Trip> getTripsByDestination(String destination) {
        return tripRepository.findTripsByDestinationContaining(destination);
    }

    public Mono<Trip> updateTrip(UUID id, Trip trip) {
        return tripRepository.findById(id)
                .flatMap(existing -> {
                    tripMapper.updateTrip(trip, existing);
                    return tripRepository.save(existing);
                });
    }

    public Mono<Void> deleteTrip(UUID id) {
        return tripRepository.deleteById(id);
    }
}