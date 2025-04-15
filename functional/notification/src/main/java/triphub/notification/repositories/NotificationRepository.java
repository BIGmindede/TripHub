package triphub.notification.repositories;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import triphub.notification.models.Notification;

public interface NotificationRepository extends ReactiveCrudRepository<Notification, UUID> {

    Flux<Notification> findByProfileId(UUID profileId);
}
