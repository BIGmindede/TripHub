package triphub.trip.DTOs;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LinkedReportDTO {
    private UUID tripId;
    private UUID authorId;
    private Integer participantsAmount;
}
