package triphub.trip.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triphub.trip.DTOs.TripAndParticipationsDTO.ParticipationAndNotificationInfo;
import triphub.trip.models.Participation;
import triphub.trip.models.constants.ParticipationStatuses;
import triphub.trip.repositories.ParticipationRepository;
import triphub.trip.repositories.TripRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final TripRepository tripRepository;
    private final RabbitService rabbitMQService;

    public Mono<Void> createParticipations(
        UUID authorId,
        ParticipationAndNotificationInfo participationAndNotificationInfo,
        UUID tripId
    ) {
        return tripRepository.findById(tripId)
            .flatMap(trip -> {
                // 1. Сохраняем участие автора
                Mono<Participation> authorParticipation = Flux.fromIterable(participationAndNotificationInfo.participations())
                    .filter(profile -> profile.participantId().equals(authorId))
                    .next()
                    .flatMap(profile -> participationRepository.save(
                        new Participation(
                            null,
                            tripId,
                            profile.participantId(),
                            ParticipationStatuses.ACCEPTED,
                            true
                        )
                    ));

                // 2. Сохраняем остальные участия и отправляем уведомления
                Flux<Participation> otherParticipations = Flux.fromIterable(participationAndNotificationInfo.participations())
                    .filter(profile -> !profile.participantId().equals(authorId))
                    .flatMap(profile -> 
                        participationRepository.save(
                            new Participation(
                                null,
                                tripId,
                                profile.participantId(),
                                ParticipationStatuses.INVITED,
                                false
                            )
                        )
                        .flatMap(participation -> 
                            rabbitMQService.sendParticipationNotification(
                                participation,
                                trip,
                                profile.email(),
                                participationAndNotificationInfo.authorTag()
                            )
                            .thenReturn(participation)
                        )
                    );

                // 3. Комбинируем оба потока и возвращаем результат
                return authorParticipation
                    .thenMany(otherParticipations)
                    .then();
            });
    }

    public Flux<Participation> getParticipationsByTripId(UUID id) {
        return participationRepository.findByTripId(id);
    }

    public Mono<Void> setOverParticipations(List<UUID> participationIds) {
        return participationRepository.disableByIds(participationIds);
    }

    
    public Mono<Participation> getParticipationByProfileIdAndTripId(UUID profileId, UUID tripId) {
        return participationRepository.findByProfileIdAndTripId(profileId, tripId);
    }

    public Mono<Participation> updateParticipation(UUID participationId, ParticipationStatuses status) {
        return participationRepository.findById(participationId)
                .flatMap(existingParticipation -> {
                    existingParticipation.setStatus(status);
                    existingParticipation.setIsCurrent(true);
                    return participationRepository.save(existingParticipation);
                });
    }

    public Mono<Void> deleteParticipations(List<UUID> participationsIds) {
        return participationRepository.deleteAllById(participationsIds);
    }
}