package mate.academy.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.booking.BookingDto;
import mate.academy.dto.booking.CreateBookingRequestDto;
import mate.academy.exception.BookingNotFoundException;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookingMapper;
import mate.academy.model.Booking;
import mate.academy.model.User;
import mate.academy.repository.booking.BookingRepository;
import org.springframework.data.domain.Page;
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
    public List<BookingDto> getAllBookings(User user,
                                               Pageable pageable) {
        Page<Booking> allOrders = bookingRepository.findAllByUserId(user.getId(), pageable);
        return bookingMapper.toBookingDto(allOrders);
    }

    @Override
    public BookingDto createBooking(CreateBookingRequestDto createBookingRequestDto) {
        Booking booking = bookingMapper.toEntity(createBookingRequestDto);

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.toDto(savedBooking);
    }

    @Override
    public List<BookingDto> getBookingsByUserIdAndStatus(Long userId,
                                                         String status, Pageable pageable) {
        return bookingRepository.findByUserIdAndStatus(userId,
                        status, pageable)
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingDto updateBooking(Long id, CreateBookingRequestDto request) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new BookingNotFoundException(id));
        bookingMapper.updateBookingFromDto(request, booking);
        bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new BookingNotFoundException(id));
        booking.setStatus(Booking.BookingStatus.valueOf("CANCELLED"));
        bookingRepository.save(booking);
    }
}
