package triphub.notification.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import triphub.notification.DTOs.ExtendedNotificationDTO;
import triphub.notification.DTOs.HtmlTemplateDTO;
import triphub.notification.DTOs.NotificationDTO;
import triphub.notification.models.Notification;
import triphub.notification.repositories.NotificationRepository;
import triphub.notification.services.helpers.EmailHelper;
import triphub.notification.services.helpers.HtmlToMailHelper;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final NotificationRepository notificationRepository;  
    private final EmailHelper emailHelper;
    private final HtmlToMailHelper htmlToMailHelper;
    private final String emailTemplateName = "Email.html";

    @RabbitListener(queues = "${spring.rabbitmq.queue.name}")
    public Mono<Void> receiveMessage(String message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message, ExtendedNotificationDTO.class))
            .flatMap(this::processNotification)
            .doOnError(error -> log.error("Failed to process notification", error))
            .onErrorResume(e -> Mono.empty()); // Продолжаем даже при ошибке
    }

    private Mono<Void> processNotification(ExtendedNotificationDTO notification) {
        return Mono.defer(() -> {
            if (notification.getNotification() == null) {
                return sendEmail(notification.getEmail(), notification.getHtmlTemplateDTO());
            } else {
                Notification notificationEntity = createNotificationEntity(notification.getNotification());
                
                return notificationRepository.save(notificationEntity)
                    .doOnSuccess(saved -> log.info("Notification saved: {}", saved))
                    .then(sendEmail(notification.getEmail(), notification.getHtmlTemplateDTO()))
                    .doOnSuccess(v -> log.info("Email sent successfully"));
            }
        });
    }

    private Notification createNotificationEntity(NotificationDTO dto) {
        Notification notification = new Notification();
        notification.setActionsFromList(dto.getActions());
        notification.setProfileId(dto.getProfileId());
        notification.setSenderTag(dto.getSenderTag());
        notification.setSentAt(dto.getSentAt());
        notification.setContent(dto.getContent());
        notification.setNotificationType(dto.getNotificationType());
        return notification;
    }

    private Mono<Void> sendEmail(String email, HtmlTemplateDTO htmlTemplateDTO) {
        return emailHelper.sendHtmlEmail(
            email,
            htmlTemplateDTO.getTitle(),
            htmlToMailHelper.buildHtml(emailTemplateName, htmlTemplateDTO)
        )
        .doOnSuccess(v -> log.info("Email sent to {}", email))
        .doOnError(e -> log.error("Failed to send email to {}", email, e));
    }
}