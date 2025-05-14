package triphub.kanban.controllers;

import java.util.List;
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
import reactor.core.publisher.Mono;
import triphub.kanban.DTOs.CreateKanbanDTO;
import triphub.kanban.models.Kanban;
import triphub.kanban.services.KanbanService;

@RestController
@RequestMapping("/kanban")
@RequiredArgsConstructor
public class KanbanController {
    private final KanbanService kanbanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Kanban> createKanban(@RequestBody CreateKanbanDTO kanban) {
        return kanbanService.createKanban(kanban.getTripId());
    }
    
    @GetMapping("/by_trip/{tripId}")
    public Mono<ResponseEntity<Kanban>> getKanbanByTripId(@PathVariable UUID tripId) {
        return kanbanService.getKanbanByTripId(tripId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/by_trip/{tripId}")
    public Mono<ResponseEntity<Void>> deleteKanbanByTripId(@PathVariable UUID tripId) {
        return kanbanService.deleteKanbanByTripId(tripId)
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }
    
    @PutMapping("/{kanbanId}")
    public Mono<ResponseEntity<Void>> updateStatuses(
            @PathVariable UUID kanbanId,
            @RequestBody List<String> statuses) {
        return kanbanService.updateKanbanStatuses(kanbanId, statuses)
                .map(Void -> ResponseEntity.noContent().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}