package triphub.trip.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExtendedNotificationDTO {
    private NotificationDTO notification;
    private String email;
    private ParticipationApproveNotifyRequest htmlTemplateDTO;
}
