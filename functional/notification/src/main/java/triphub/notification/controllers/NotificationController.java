package triphub.notification.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triphub.notification.DTOs.ExtendedNotificationDTO;
import triphub.notification.models.Notification;
import triphub.notification.services.NotificationService;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Notification> createAndSendNotification(
            @RequestBody ExtendedNotificationDTO dto) {
        return notificationService.createAndSendNotification(
                dto.getNotification(),
                dto.getEmail(),
                dto.getHtmlTemplateDTO()
        );
    }

    @PostMapping("/email")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Void>> sendEmailNotification(
            @RequestBody ExtendedNotificationDTO dto) {
        return notificationService.sendEmail(dto.getEmail(), dto.getHtmlTemplateDTO())
                .thenReturn(ResponseEntity.ok().build());
    }

    @GetMapping("/by_profile/{profileId}")
    public Flux<Notification> getNotificationsByProfileId(
            @PathVariable UUID profileId) {
        return notificationService.getNotificationsByProfileId(profileId);
    }

    @PutMapping("/{notificationId}")
    public Mono<ResponseEntity<Void>> markAsRead(
            @PathVariable UUID notificationId) {
        return notificationService.markAsRead(notificationId)
            .map(Void -> ResponseEntity.noContent().<Void>build())
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{notificationId}")
    public Mono<ResponseEntity<Void>> deleteNotification(
            @PathVariable UUID notificationId) {
        return notificationService.deleteNotification(notificationId)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
