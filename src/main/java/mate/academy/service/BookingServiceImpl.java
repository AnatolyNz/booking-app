package mate.academy.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.booking.BookingDto;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookingMapper;
import mate.academy.repository.booking.BookingRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto getBookingById(Long id) {
        return bookingMapper.toDto(bookingRepository.getBookingById(id)
                .orElseThrow(() -> new
                        EntityNotFoundException("Can't find booking with id " + id)));
    }

    @Override
    public List<BookingDto> findAll(Pageable pageable) {
        return null;
    }
}
