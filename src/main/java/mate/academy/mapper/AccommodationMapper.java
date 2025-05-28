package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.accommodation.AccommodationDto;
import mate.academy.dto.accommodation.CreateAccommodationRequestDto;
import mate.academy.dto.accommodation.UpdateAccommodationRequestDto;
import mate.academy.model.Accommodation;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(config = MapperConfig.class)
public interface AccommodationMapper {

    AccommodationDto toDto(Accommodation accommodation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Accommodation toModel(CreateAccommodationRequestDto requestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookings", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateAccommodationFromDto(UpdateAccommodationRequestDto dto,
                                    @MappingTarget Accommodation entity);
}
