package triphub.report.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import triphub.report.models.Report;

@Service
@RequiredArgsConstructor
public class ReportConsumer {

    private final ReportService reportService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${spring.rabbitmq.queue.name}")
    public Mono<Void> receiveMessage(String message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message, Report.class))
                .flatMap(report -> reportService.createReport(report))
                .then();
    }
}