package triphub.kanban.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import triphub.kanban.DTOs.CreateKanbanRabbitDTO;

@Service
@RequiredArgsConstructor
public class KanbanConsumer {

    private final KanbanService kanbanService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${spring.rabbitmq.queue.name}")
    public Mono<Void> receiveMessage(String message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message, CreateKanbanRabbitDTO.class))
                .flatMap(kanban -> kanbanService.createKanban(kanban.getTripId()))
                .then();
    }
}