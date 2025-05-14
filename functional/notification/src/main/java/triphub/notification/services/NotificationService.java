package triphub.notification.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triphub.notification.DTOs.ExtendedNotificationDTO;
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
