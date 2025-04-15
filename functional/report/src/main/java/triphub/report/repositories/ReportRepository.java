package triphub.report.repositories;

import triphub.report.models.Report;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


public interface ReportRepository extends ReactiveCrudRepository<Report, UUID> {
    @Query("SELECT * FROM reports WHERE arrival_to ILIKE '%' || :arrivalTo || '%'")
    Flux<Report> findReportsByArrivalToContaining(String arrivalTo);
    Mono<Report> findReportByTripId(UUID tripId);
}