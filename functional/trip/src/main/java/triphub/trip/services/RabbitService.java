package triphub.trip.services;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import triphub.trip.DTOs.ExtendedNotificationDTO;
import triphub.trip.DTOs.LinkedKanbanDTO;
import triphub.trip.DTOs.LinkedReportDTO;
import triphub.trip.DTOs.NotificationDTO;
import triphub.trip.DTOs.ParticipationApproveNotifyRequest;
import triphub.trip.DTOs.constants.NotificationStatuses;
import triphub.trip.DTOs.constants.NotificationTypes;
import triphub.trip.configs.rabbitConfig.RabbitConfiguration;
import triphub.trip.models.Trip;
import triphub.trip.models.Participation;
@Service
@RequiredArgsConstructor
public class RabbitService {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfiguration rabbitMQConfig;
    private final ObjectMapper objectMapper;

    @Value("${trip-service.base-url}")
    private String tripServiceBaseUrl;

    public Mono<Void> sendParticipationNotification(Participation participation, Trip trip, String email, String authorTag) {
        return Mono.fromCallable(() -> {
            ParticipationApproveNotifyRequest.Action accept = new ParticipationApproveNotifyRequest.Action(
                "Принять",
                tripServiceBaseUrl + "/participations/accept/" + participation.getId(),
                "ACCEPT"
            );

            ParticipationApproveNotifyRequest.Action decline = new ParticipationApproveNotifyRequest.Action(
                "Отклонить",
                tripServiceBaseUrl + "/participations/decline/" + participation.getId(),
                "DECLINE"
            );

            ParticipationApproveNotifyRequest htmlTemplate = new ParticipationApproveNotifyRequest(
                "Вас пригласили в путешествие",
                "Пожалуйста, примите или отклоните приглашение пользователя",
                List.of(accept, decline)
            );

            ExtendedNotificationDTO notification = new ExtendedNotificationDTO(
                new NotificationDTO(
                    participation.getProfileId(),
                    authorTag,
                    Instant.now(),
                    "Пользователь " + authorTag + " пригласил вас в путешествие в " + trip.getDestination(),
                    NotificationTypes.TRIP_INVITATION,
                    NotificationStatuses.NEW,
                    List.of(accept, decline)
                ),
                email,
                htmlTemplate
            );

            rabbitTemplate.convertAndSend(
                rabbitMQConfig.getExchangeNotifications(),
                rabbitMQConfig.getRoutingKeyNotifications(),
                objectMapper.writeValueAsString(notification)
            );
            return null;
        }).then();
    }

    public Mono<Void> createLinkedReport(Trip trip, Integer participantsAmount) {
        return Mono.fromCallable(() -> {
            LinkedReportDTO report = new LinkedReportDTO(
                trip.getId(),
                trip.getAuthorId(),
                participantsAmount
            );

            rabbitTemplate.convertAndSend(
                rabbitMQConfig.getExchangeReports(),
                rabbitMQConfig.getRoutingKeyReports(),
                objectMapper.writeValueAsString(report)
            );
            return null;
        }).then();
    }

    public Mono<Void> createLinkedKanban(Trip trip) {
        return Mono.fromCallable(() -> {

            LinkedKanbanDTO kanban = new LinkedKanbanDTO(trip.getId());

            rabbitTemplate.convertAndSend(
                rabbitMQConfig.getExchangeKanban(),
                rabbitMQConfig.getRoutingKeyKanban(),
                objectMapper.writeValueAsString(kanban)
            );
            return null;
        }).then();
    }
}

