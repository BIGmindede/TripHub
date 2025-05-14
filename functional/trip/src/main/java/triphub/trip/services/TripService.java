package triphub.trip.services;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triphub.trip.DTOs.TripStatisticsDTO;
import triphub.trip.mappers.TripMapper;
import triphub.trip.models.Trip;
import triphub.trip.models.constants.ParticipationStatuses;
import triphub.trip.repositories.ParticipationRepository;
import triphub.trip.repositories.TripRepository;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.UUID;

import triphub.trip.models.Participation;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;
    private final RabbitService rabbitMQService;
    private final ParticipationRepository participationRepository;

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

    public Mono<TripStatisticsDTO> getTripsStatisticsByProfileId(UUID id) {
    return tripRepository.findTripsByProfileId(id, ParticipationStatuses.ACCEPTED)
        .collectList()
        .map(trips -> {
            if (trips.isEmpty()) {
                return new TripStatisticsDTO(); // Пустая статистика, если поездок нет
            }
            
            TripStatisticsDTO dto = new TripStatisticsDTO();

            // 1. Общее количество поездок
            dto.setTotalTrips(trips.size());

            // 2. Уникальные места (дестинации)
            dto.setUniqueDestinations(
                trips.stream()
                    .map(Trip::getDestination)
                    .distinct()
                    .count()
            );

            // 3. Самое долгое путешествие (по количеству дней)
            Trip longestTrip = trips.stream()
                .max(Comparator.comparingLong(trip -> 
                    ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate())
                ))
                .orElse(null);
            dto.setLongestTrip(longestTrip);

            // 4. Общее время в путешествиях (сумма дней всех поездок)
            dto.setTotalDaysInTrips(
                trips.stream()
                    .mapToLong(trip -> 
                        ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate())
                    )
                    .sum()
            );

            // 5. Последняя поездка (по дате окончания)
            Trip lastTrip = trips.stream()
                .max(Comparator.comparing(Trip::getEndDate))
                .orElse(null);
            dto.setLastTrip(lastTrip);

            return dto;
        });
    }

    public Mono<Trip> getCurrentTrip(UUID profileId) {
        return participationRepository.findByProfileIdAndIsCurrent(profileId, true)
            .flatMap(participation -> tripRepository.findById(participation.getTripId()));
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