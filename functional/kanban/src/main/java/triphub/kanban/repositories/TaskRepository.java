package triphub.kanban.repositories;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import reactor.core.publisher.Flux;
import triphub.kanban.models.Task;

public interface TaskRepository extends R2dbcRepository<Task, UUID> {
    
    @Query("""
        SELECT t.* FROM tasks t
        JOIN kanban k ON t.kanban_id = k.id
        WHERE t.implementer_id = :authorId 
        AND k.trip_id = :tripId
        """)
    Flux<Task> findByImplementerIdAndTripId(UUID implementerId, UUID tripId);
    Flux<Task> findByKanbanId(UUID kanbanId);
}
