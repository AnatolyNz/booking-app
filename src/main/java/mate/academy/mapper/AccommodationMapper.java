package mate.academy.mapper;

import mate.academy.dto.AccommodationDto;
import mate.academy.dto.CreateAccommodationRequestDto;
import mate.academy.model.Accommodation;

public interface AccommodationMapper {
    AccommodationDto toDto(Accommodation accommodation);

    Accommodation toModel(CreateAccommodationRequestDto requestDto);

    Accommodation toEntity(CreateAccommodationRequestDto bookDto);
}
