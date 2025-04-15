package triphub.report.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import triphub.report.models.Report;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ReportMapper {
    @Mappings({
        @Mapping(target = "id", ignore = true)
    })
    void updateReport(Report source, @MappingTarget Report target);
}