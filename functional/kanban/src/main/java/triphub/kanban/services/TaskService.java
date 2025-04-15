package triphub.kanban.services;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triphub.kanban.mappers.TaskMapper;
import triphub.kanban.models.Task;
import triphub.kanban.repositories.TaskRepository;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public Mono<Task> createTask(Task task) {
        task.setCreatedAt(LocalDate.now());
        return taskRepository.save(task);
    }
    
    public Flux<Task> getTasksByImplementerAndTrip(UUID implementerId, UUID tripId) {
        return taskRepository.findByImplementerIdAndTripId(implementerId, tripId);
    }
    
    public Flux<Task> getTasksByKanbanId(UUID kanbanId) {
        return taskRepository.findByKanbanId(kanbanId);
    }
    
    public Mono<Task> getTaskById(UUID taskId) {
        return taskRepository.findById(taskId);
    }
    
    public Mono<Task> updateTask(UUID taskId, Task task) {
        return taskRepository.findById(taskId)
            .flatMap(existing -> {
                taskMapper.updateTask(task, existing);
                return taskRepository.save(existing);
            });
    }
    
    public Mono<Void> deleteTask(UUID taskId) {
        return taskRepository.deleteById(taskId);
    }
}
