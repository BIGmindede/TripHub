package triphub.trip.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import triphub.trip.DTOs.constants.NotificationStatuses;
import triphub.trip.DTOs.constants.NotificationTypes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class NotificationDTO {
    private UUID profileId;
    private String senderTag;
    private Instant sentAt;
    private String content;
    private NotificationTypes notificationType;
    private NotificationStatuses status;
    private List<ParticipationApproveNotifyRequest.Action> actions;
}

