package triphub.kanban.controllers;

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
import triphub.kanban.models.Task;
import triphub.kanban.services.TaskService;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Task> createTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }
    
    @GetMapping("/by_implementer_trip/{implementerId}/{tripId}")
    public Flux<Task> getTasksByImplementerAndTrip(
            @PathVariable UUID implementerId,
            @PathVariable UUID tripId) {
        return taskService.getTasksByImplementerAndTrip(implementerId, tripId);
    }
    
    @GetMapping("/by_kanban/{kanbanId}")
    public Flux<Task> getTasksByKanbanId(@PathVariable UUID kanbanId) {
        return taskService.getTasksByKanbanId(kanbanId);
    }
    
    @GetMapping("/{taskId}")
    public Mono<ResponseEntity<Task>> getTaskById(@PathVariable UUID taskId) {
        return taskService.getTaskById(taskId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{taskId}")
    public Mono<ResponseEntity<Void>> updateTask(
            @PathVariable UUID taskId,
            @RequestBody Task taskUpdates) {
        return taskService.updateTask(taskId, taskUpdates)
                .map(Void -> ResponseEntity.noContent().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{taskId}")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable UUID taskId) {
        return taskService.deleteTask(taskId)
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }
}
