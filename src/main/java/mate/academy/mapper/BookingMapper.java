package mate.academy.mapper;

import mate.academy.config.MapperConfig;
import mate.academy.dto.booking.BookingDto;
import mate.academy.dto.booking.CreateBookingRequestDto;
import mate.academy.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookingMapper {
    @Mapping(source = "accommodation.id", target = "accommodationId")
    BookingDto toDto(Booking booking);

    @Mapping(source = "accommodationId", target = "accommodation.id")
    Booking toEntity(CreateBookingRequestDto requestDto);

    @Mapping(source = "accommodationId", target = "accommodation.id")
    void updateBookingFromDto(CreateBookingRequestDto requestDto,
                              @MappingTarget Booking booking);
}
