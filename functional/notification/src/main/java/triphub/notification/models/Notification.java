package triphub.notification.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import triphub.notification.models.constants.NotificationStatuses;
import triphub.notification.models.constants.NotificationTypes;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import triphub.notification.DTOs.HtmlTemplateDTO;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.core.type.TypeReference;

import triphub.notification.DTOs.NotificationDTO;

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
    @Column("notification_type")
    private NotificationTypes notificationType;
    @Column("notification_status")
    private NotificationStatuses status = NotificationStatuses.NEW;
    private String actions;

    @Transient
    public void setActionsFromList(List<HtmlTemplateDTO.Action> actions) {
        try {
            this.actions = new ObjectMapper().writeValueAsString(actions);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize actions", e);
        }
    }
    
    @Transient
    public List<HtmlTemplateDTO.Action> getActionsAsList() {
        try {
            return new ObjectMapper().readValue(
                this.actions,
                new TypeReference<List<HtmlTemplateDTO.Action>>(){}
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse actions", e);
        }
    }
}
