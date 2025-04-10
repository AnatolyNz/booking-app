package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.accommodation.AccommodationDto;
import mate.academy.dto.accommodation.CreateAccommodationRequestDto;
import mate.academy.model.Accommodation;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface AccommodationMapper {
    AccommodationDto toDto(Accommodation accommodation);

    Accommodation toModel(CreateAccommodationRequestDto requestDto);

    Accommodation toEntity(CreateAccommodationRequestDto bookDto);
}
