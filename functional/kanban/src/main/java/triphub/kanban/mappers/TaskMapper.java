package triphub.kanban.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import triphub.kanban.models.Task;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {
    @Mappings({
        @Mapping(target = "id", ignore = true)
    })
    void updateTask(Task source, @MappingTarget Task target);
}
