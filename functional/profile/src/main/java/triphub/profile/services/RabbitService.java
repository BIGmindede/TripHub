package triphub.profile.services;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import triphub.profile.DTOs.ExtendedNotificationDTO;
import triphub.profile.DTOs.RegisterApproveNotifyRequest;
import triphub.profile.configs.rabbitConfig.RabbitConfiguration;
import triphub.profile.models.Profile;

@Service
@RequiredArgsConstructor
public class RabbitService {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfiguration rabbitMQConfig;
    private final ObjectMapper objectMapper;

    @Value("${security-service.base-url}")
    private String securityServiceBaseUrl;

    public Mono<Void> sendRegistrationNotification(Profile profile) {
        return Mono.fromCallable(() -> {
            RegisterApproveNotifyRequest.Action action = new RegisterApproveNotifyRequest.Action(
                "Подтвердить аккаунт",
                securityServiceBaseUrl + "/approve/" + profile.getId(),
                "ACCEPT"
            );

            RegisterApproveNotifyRequest htmlTemplate = new RegisterApproveNotifyRequest(
                "Завершение регистрации",
                "Пожалуйста подтвердите регистрацию нажав на кнопку ниже",
                List.of(action)
            );

            ExtendedNotificationDTO notification = new ExtendedNotificationDTO(
                profile.getEmail(),
                htmlTemplate
            );

            rabbitTemplate.convertAndSend(
                rabbitMQConfig.getExchange(),
                rabbitMQConfig.getRoutingKey(),
                objectMapper.writeValueAsString(notification)
            );
            return null;
        }).then();
    }
}
