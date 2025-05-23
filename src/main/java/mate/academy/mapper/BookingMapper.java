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
    @Mapping(source = "user.id", target = "userId")
    BookingDto toDto(Booking booking);

    @Mapping(source = "accommodationId", target = "accommodation.id")
    @Mapping(source = "userId", target = "user.id")
    Booking toEntity(CreateBookingRequestDto requestDto);

    @Mapping(source = "accommodationId", target = "accommodation.id")
    @Mapping(source = "userId", target = "user.id")
    void updateBookingFromDto(CreateBookingRequestDto requestDto,
                              @MappingTarget Booking booking);
}
