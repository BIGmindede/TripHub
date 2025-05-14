package triphub.report.services;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triphub.report.mappers.ReportMapper;
import triphub.report.models.Report;
import triphub.report.repositories.ReportRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;

    public Mono<Report> createReport(Report report) {
        return reportRepository.save(report);
    }

    public Mono<Report> getReportById(UUID id) {
        return reportRepository.findById(id);
    }

    public Mono<Report> getReportsByTripId(UUID tripId) {
        return reportRepository.findReportByTripId(tripId);
    }

    public Flux<Report> getReportsByArrivalTo(String arrivalTo) {
        return reportRepository.findReportsByArrivalToContaining(arrivalTo);
    }

    public Flux<Report> getReports() {
        return reportRepository.findAll();
    }

    public Mono<Report> updateReportByTripId(UUID tripId, Report report) {
        return reportRepository.findByTripId(tripId)
            .flatMap(existing -> {
                reportMapper.updateReport(report, existing);
                return reportRepository.save(existing);
            });
    }

    public Mono<Report> updateReport(UUID id, Report report) {
        return reportRepository.findById(id)
            .flatMap(existing -> {
                reportMapper.updateReport(report, existing);
                return reportRepository.save(existing);
            });
    }

    public Mono<Void> deleteReport(UUID id) {
        return reportRepository.deleteById(id);
    }
}