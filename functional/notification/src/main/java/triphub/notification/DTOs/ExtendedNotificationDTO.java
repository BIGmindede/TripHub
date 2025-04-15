package triphub.notification.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import triphub.notification.models.Notification;

@Data
@AllArgsConstructor
public class ExtendedNotificationDTO {
    private Notification notification;
    private String email;
    private HtmlTemplateDTO htmlTemplateDTO;
}
