package triphub.trip.DTOs;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import triphub.trip.models.Trip;

@Data
public class TripAndParticipationsDTO {
    private UUID tripId = null;
    private String status;
    private String[] statuses;
    private UUID authorId;
    private String destination;
    private LocalDate startDate;
    private String thumbnailUrl;

    private List<ProfilePart> participations;

    public record ProfilePart(UUID participantId, String email) {}
    
    public Trip toTrip() {
        return new Trip(
            tripId,
            status,
            statuses,
            authorId,
            destination,
            startDate,
            thumbnailUrl
        );
    }
}
