package triphub.notification.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import triphub.notification.models.constants.NotificationStatuses;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("notifications")
public class Notification {
    @Id
    private UUID id;
    private UUID profileId;
    private String senderTag;
    private Instant sentAt;
    private String content;
    @Column("notification_status")
    private NotificationStatuses status = NotificationStatuses.NEW;
}
