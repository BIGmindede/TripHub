package triphub.report.controllers;

import org.springframework.http.ResponseEntity;
import triphub.report.models.Report;
import triphub.report.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Report> createReport(@RequestBody Report report) {
        return reportService.createReport(report);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Report>> getReportById(@PathVariable UUID id) {
        return reportService.getReportById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/by_trip/{tripId}")
    public Mono<ResponseEntity<Report>> getReportsByArrivalTo(@PathVariable UUID tripId) {
        return reportService.getReportsByTripId(tripId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/by_arrivalto/{arrivalTo}")
    public Flux<Report> getReportsByArrivalTo(@PathVariable String arrivalTo) {
        return reportService.getReportsByArrivalTo(arrivalTo);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateReport(@PathVariable UUID id, @RequestBody Report report) {
        return reportService.updateReport(id, report)
                .map(Void -> ResponseEntity.noContent().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteReport(@PathVariable UUID id) {
        return reportService.deleteReport(id)
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }
}