package mate.academy.service;

import java.util.List;
import mate.academy.dto.booking.BookingDto;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingDto getBookingById(Long id);

    List<BookingDto> findAll(Pageable pageable);
}
