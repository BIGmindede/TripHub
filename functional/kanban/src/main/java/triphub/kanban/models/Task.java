package triphub.kanban.models;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("tasks")
public class Task {

    @Id
    private UUID id;
    private UUID kanbanId;
    private UUID authorId;
    private UUID implementerId;
    private int statusId;
    private LocalDate createdAt;
    private LocalDate targetDate;
    private String name;
    private String description;
}
