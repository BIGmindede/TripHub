package triphub.notification.DTOs;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import triphub.notification.models.constants.NotificationStatuses;
import triphub.notification.models.constants.NotificationTypes;

@Data
public class NotificationDTO {
    private UUID id;
    private UUID profileId;
    private String senderTag;
    private Instant sentAt;
    private String content;
    private NotificationTypes notificationType;
    private NotificationStatuses status = NotificationStatuses.NEW;
    private List<HtmlTemplateDTO.Action> actions;
}
