package triphub.trip.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExtendedNotificationDTO {
    private String email;
    private ParticipationApproveNotifyRequest htmlTemplateDTO;
}
