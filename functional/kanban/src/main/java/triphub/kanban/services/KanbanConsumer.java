package triphub.kanban.services;

import java.nio.charset.StandardCharsets;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import triphub.kanban.DTOs.CreateKanbanDTO;

@Service
@RequiredArgsConstructor
public class KanbanConsumer {

    private final KanbanService kanbanService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${spring.rabbitmq.queue.name}")
    public Mono<Void> receiveMessage(String message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message, CreateKanbanDTO.class))
            .flatMap(kanbanDTO -> kanbanService.createKanban(kanbanDTO.getTripId()))
            .then();
    }
}