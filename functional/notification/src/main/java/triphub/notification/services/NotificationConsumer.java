package triphub.notification.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import triphub.notification.DTOs.ExtendedNotificationDTO;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${spring.rabbitmq.queue.name}")
    public void receiveMessage(String message) {
        try {
            ExtendedNotificationDTO notification = objectMapper.readValue(message, ExtendedNotificationDTO.class);
            notificationService.sendEmail(
                notification.getEmail(),
                notification.getHtmlTemplateDTO()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to process notification", e);
        }
    }
}