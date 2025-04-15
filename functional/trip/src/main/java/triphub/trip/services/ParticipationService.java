package triphub.trip.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triphub.trip.DTOs.TripAndParticipationsDTO.ProfilePart;
import triphub.trip.models.Participation;
import triphub.trip.models.constants.ParticipationStatuses;
import triphub.trip.repositories.ParticipationRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final RabbitService rabbitMQService;

    public Mono<Void> createParticipations(UUID authorId, List<ProfilePart> profiles, UUID tripId) {
        return Flux.fromIterable(profiles).flatMap(profile -> {
            if (profile.participantId().equals(authorId))
                return participationRepository.save(new Participation(null, tripId, profile.participantId(), ParticipationStatuses.ACCEPTED));
            return participationRepository.save(new Participation(null, tripId, profile.participantId(), ParticipationStatuses.INVITED))
                    .flatMap(participation -> 
                            rabbitMQService.sendParticipationNotification(
                                participation.getId(),
                                profile.email()
                            )
                            .thenReturn(participation)
                    );
        }).then();
    }

    public Flux<Participation> getParticipationsByTripId(UUID id) {
        return participationRepository.findByTripId(id);
    }

    public Mono<Participation> updateParticipation(UUID participationId, ParticipationStatuses status) {
        return participationRepository.findById(participationId)
                .flatMap(existingParticipation -> {
                    existingParticipation.setStatus(status);
                    return participationRepository.save(existingParticipation);
                });
    }

    public Mono<Void> deleteParticipations(List<UUID> participationsIds) {
        return participationRepository.deleteAllById(participationsIds);
    }
}