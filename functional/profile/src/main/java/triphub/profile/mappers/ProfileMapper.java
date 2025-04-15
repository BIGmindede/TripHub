package triphub.profile.mappers;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;

import triphub.profile.DTOs.ProfileDTO;
import triphub.profile.models.Profile;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProfileMapper {
    @Mappings({
        @Mapping(target = "id", ignore = true)
    })
    void updateProfile(Profile source, @MappingTarget Profile target);

    ProfileDTO map(Profile profile);

    @InheritInverseConfiguration
    Profile map(ProfileDTO dto);
}