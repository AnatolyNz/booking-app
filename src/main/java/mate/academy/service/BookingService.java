package mate.academy.service;

import java.util.List;
import mate.academy.dto.booking.BookingDto;
import mate.academy.dto.booking.CreateBookingRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingDto getBookingById(Long id);

    List<BookingDto> findAll(Pageable pageable);

    BookingDto createBooking(CreateBookingRequestDto bookingDto);

    List<BookingDto> getBookingsByUserIdAndStatus(Long userId, String status, Pageable pageable);

    BookingDto updateBooking(Long id, CreateBookingRequestDto request);

    void cancelBooking(Long id);
}
