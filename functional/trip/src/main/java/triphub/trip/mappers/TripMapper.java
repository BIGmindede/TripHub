package triphub.trip.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import triphub.trip.models.Trip;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TripMapper {
    @Mappings({
        @Mapping(target = "id", ignore = true)
    })
    void updateTrip(Trip source, @MappingTarget Trip target);
}