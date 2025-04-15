package triphub.trip.DTOs;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LinkedKanbanDTO {
    private UUID tripId;
}

