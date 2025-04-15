package triphub.kanban.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import triphub.kanban.models.Kanban;
import triphub.kanban.repositories.KanbanRepository;

@Service
@RequiredArgsConstructor
public class KanbanService {
    private final KanbanRepository kanbanRepository;

    public Mono<Kanban> createKanban(UUID tripId) {
        return Mono.fromCallable(() -> {
            Kanban kanban = new Kanban();
            kanban.setTripId(tripId);
            return kanban;
        })
        .flatMap(kanbanRepository::save);
    }
    
    public Mono<Kanban> getKanbanByTripId(UUID tripId) {
        return kanbanRepository.findByTripId(tripId);
    }
    
    public Mono<Void> deleteKanbanByTripId(UUID tripId) {
        return kanbanRepository.deleteByTripId(tripId);
    }
    
    public Mono<Kanban> updateKanbanStatuses(UUID kanbanId, List<String> statuses) {
        return kanbanRepository.findById(kanbanId)
                .flatMap(existing -> {
                    existing.setStatuses(statuses);
                    return kanbanRepository.save(existing);
                });
    }
}
