package triphub.notification.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triphub.notification.DTOs.HtmlTemplateDTO;
import triphub.notification.models.Notification;
import triphub.notification.models.constants.NotificationStatuses;
import triphub.notification.repositories.NotificationRepository;
import triphub.notification.services.helpers.EmailHelper;
import triphub.notification.services.helpers.HtmlToMailHelper;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailHelper emailHelper;
    private final HtmlToMailHelper htmlToMailHelper;
    private final String emailTemplateName = "Email.html";

    public Mono<Notification> createAndSendNotification(Notification notification, String email, HtmlTemplateDTO htmlTemplateDTO) {
        return notificationRepository.save(notification)
            .flatMap(savedNotification -> {
                return Mono.just(emailHelper
                    .sendHtmlEmail(
                        email,
                        htmlTemplateDTO.getTitle(),
                        htmlToMailHelper.buildHtml(
                            emailTemplateName,
                            htmlTemplateDTO))
                    .subscribe()
                ).thenReturn(savedNotification);
            });
    }

    public Mono<Void> sendEmail(String email, HtmlTemplateDTO htmlTemplateDTO) {
        System.out.println(htmlTemplateDTO);
        return Mono.just(emailHelper
                    .sendHtmlEmail(
                        email,
                        htmlTemplateDTO.getTitle(),
                        htmlToMailHelper.buildHtml(
                            emailTemplateName,
                            htmlTemplateDTO))
                    .subscribe()
                ).then();
    }

    public Flux<Notification> getNotificationsByProfileId(UUID profileId) {
        return notificationRepository.findByProfileId(profileId);
    }

    public Mono<Notification> markAsRead(UUID notificationId) {
        return notificationRepository.findById(notificationId)
                .flatMap(notification -> {
                    notification.setStatus(NotificationStatuses.READ);
                    return notificationRepository.save(notification);
                });
    }

    public Mono<Void> deleteNotification(UUID notificationId) {
        return notificationRepository.deleteById(notificationId);
    }
}
