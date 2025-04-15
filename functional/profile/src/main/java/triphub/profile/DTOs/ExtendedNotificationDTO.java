package triphub.profile.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExtendedNotificationDTO {
    private String email;
    private RegisterApproveNotifyRequest htmlTemplateDTO;
}
