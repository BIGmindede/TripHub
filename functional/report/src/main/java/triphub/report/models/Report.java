package triphub.report.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("reports")
public class Report {
    @Id
    private UUID id;
    private UUID tripId;
    private UUID authorId;
    private String departureFrom;
    private String arrivalTo;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer participantsAmount;
    private Double sumExpenses;
    private Double avgExpenses;
    private String forwardVehicle;
    private String backVehicle;
    private String plannedBudget;
    private String totalBudget;
    private String equipmentTaken;
    private String notes;
    private Boolean isPublished;
}