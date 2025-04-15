package triphub.kanban.repositories;

import java.util.UUID;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import reactor.core.publisher.Mono;
import triphub.kanban.models.Kanban;


public interface KanbanRepository extends R2dbcRepository<Kanban, UUID> {
    Mono<Void> deleteByTripId(UUID tripId);
    Mono<Kanban> findByTripId(UUID tripId);
}
