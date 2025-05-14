package triphub.trip.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@Table("trips")
public class Trip {
    @Id
    private UUID id;
    @Column("status_id")
    private int statusId;
    private String[] statuses;
    @Column("author_id")
    private UUID authorId;
    private String destination;
    private LocalDate startDate;
    private String thumbnailUrl;
    private LocalDate endDate;
}