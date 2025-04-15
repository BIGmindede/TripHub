package triphub.trip.models;

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
@Table("trips")
public class Trip {
    @Id
    private UUID id;
    private String status;
    private String[] statuses;
    private UUID authorId;
    private String destination;
    private LocalDate startDate;
    private String thumbnailUrl;
}