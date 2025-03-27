package mate.academy.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.booking.BookingDto;
import mate.academy.dto.booking.CreateBookingRequestDto;
import mate.academy.exception.BookingAlreadyCancelledException;
import mate.academy.exception.BookingAlreadyExistsException;
import mate.academy.exception.BookingNotFoundException;
import mate.academy.exception.EntityNotFoundException;
import mate.academy.mapper.BookingMapper;
import mate.academy.model.Booking;
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

    public List<BookingDto> getBookingsByUserId(Long userId, Pageable pageable) {
        return bookingRepository.findAllByUserId(userId, pageable)
                .stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsWithoutUserId(Pageable pageable) {
        Page<Booking> allOrders = bookingRepository.findAll(pageable);
        return bookingMapper.toBookingDto(allOrders);
    }

    @Override
    public BookingDto createBooking(CreateBookingRequestDto createBookingRequestDto) {
        Long accommodationId = createBookingRequestDto.getAccommodationId();
        LocalDate checkInDate = createBookingRequestDto.getCheckInDate();
        LocalDate checkOutDate = createBookingRequestDto.getCheckOutDate();

        boolean isBooked = false;
        for (LocalDate date = checkInDate; !date.isAfter(checkOutDate); date = date.plusDays(1)) {
            if (bookingRepository
                    .existsByAccommodationIdAndCheckInDateBeforeAndCheckOutDateAfter(
                            accommodationId, date, date)) {
                isBooked = true;
                break;
            }
        }

        if (isBooked) {
            throw new BookingAlreadyExistsException(
                    "Accommodation is already booked during the selected period");
        }

        Booking booking = bookingMapper.toEntity(createBookingRequestDto);

        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.toDto(savedBooking);
    }

    @Override
    public List<BookingDto> getBookingsByUserIdAndStatus(
            Long userId, Booking.BookingStatus status, Pageable pageable) {
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

        Long accommodationId = request.getAccommodationId();
        LocalDate newCheckInDate = request.getCheckInDate();
        LocalDate newCheckOutDate = request.getCheckOutDate();

        boolean isBooked = false;
        for (LocalDate date = newCheckInDate;
                !date.isAfter(newCheckOutDate); date = date.plusDays(1)) {
            if (bookingRepository
                    .existsByAccommodationIdAndCheckInDateBeforeAndCheckOutDateAfter(
                            accommodationId, date, date)) {
                isBooked = true;
                break;
            }
        }

        if (isBooked) {
            throw new BookingAlreadyExistsException(
                    "Accommodation is already booked during the selected period");
        }

        bookingMapper.updateBookingFromDto(request, booking);
        bookingRepository.save(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new BookingNotFoundException(id));
        if (booking.getStatus() == Booking.BookingStatus.valueOf("CANCELED")) {
            throw new BookingAlreadyCancelledException("This booking has already been canceled.");
        }
        booking.setStatus(Booking.BookingStatus.valueOf("CANCELED"));
        bookingRepository.save(booking);
    }
}
