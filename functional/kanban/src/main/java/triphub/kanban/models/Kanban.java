package triphub.kanban.models;

import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Table("kanban")
public class Kanban {

    @Id
    private UUID id;
    private UUID tripId;
    private List<String> statuses = List.of("Новая", "В работе", "Выполнена");
}
