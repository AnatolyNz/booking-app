package mate.academy.service;

import mate.academy.dto.booking.BookingDto;
import mate.academy.dto.booking.CreateBookingRequestDto;
import mate.academy.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingDto getBookingById(Long id);

    Page<BookingDto> getBookingsByUserId(Long userId, Pageable pageable);

    Page<BookingDto> getAllBookingsWithoutUserId(Pageable pageable);

    BookingDto createBooking(CreateBookingRequestDto bookingDto, Long userId);

    Page<BookingDto> getBookingsByUserIdAndStatus(Long userId,
                                                  Booking.BookingStatus status, Pageable pageable);

    BookingDto updateBooking(Long id, CreateBookingRequestDto request);

    void cancelBooking(Long id);
}
