package triphub.trip.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import triphub.trip.models.constants.ParticipationStatuses;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("participations")
public class Participation {
    @Id
    private UUID id;
    private UUID tripId;
    private UUID profileId;
    private ParticipationStatuses status;
    private Boolean isCurrent;
}