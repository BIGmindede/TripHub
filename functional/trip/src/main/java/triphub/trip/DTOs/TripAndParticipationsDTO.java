package triphub.trip.DTOs;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import triphub.trip.models.Trip;

@Data
public class TripAndParticipationsDTO {
    private Trip trip;
    private ParticipationAndNotificationInfo participationAndNotificationInfo;

    public record ProfilePart(UUID participantId, String email) {}

    public record ParticipationAndNotificationInfo(
        List<ProfilePart> participations,
        String authorTag
    ) {}
}
