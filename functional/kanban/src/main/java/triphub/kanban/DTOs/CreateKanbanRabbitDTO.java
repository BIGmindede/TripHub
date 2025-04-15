package triphub.kanban.DTOs;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateKanbanRabbitDTO {
    UUID tripId;
}
