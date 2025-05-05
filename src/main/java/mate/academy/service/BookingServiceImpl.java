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
import mate.academy.model.Accommodation;
import mate.academy.model.Booking;
import mate.academy.model.User;
import mate.academy.repository.PaymentRepository;
import mate.academy.repository.booking.BookingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;
    private final PaymentRepository paymentRepository;

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
    public BookingDto createBooking(CreateBookingRequestDto request, User user) {
        // Check pending payments
        long pendingPayments = paymentRepository.countPendingPaymentsByUserId(user.getId());
        if (pendingPayments > 0) {
            throw new IllegalStateException(
                    "You have pending payments. Please pay them before booking.");
        }

        // Check for date conflicts
        for (LocalDate date = request.getCheckInDate();
                     !date.isAfter(request.getCheckOutDate());
                     date = date.plusDays(1)) {
            boolean isBooked = bookingRepository
                     .existsByAccommodationIdAndCheckInDateBeforeAndCheckOutDateAfter(
                     request.getAccommodationId(), date, date);
            if (isBooked) {
                throw new BookingAlreadyExistsException(
                        "Accommodation is already booked during the selected period");
            }
        }

        // Map and set user
        Booking booking = bookingMapper.toEntity(request);
        booking.setUser(user);

        // Save and return DTO
        Booking savedBooking = bookingRepository.save(booking);

        // Send notification
        String message = String.format(
                "New booking created:\nUser: %s\nAccommodation: %s\nCheck-in: %s\nCheck-out: %s",
                savedBooking.getUser().getUsername(),
                savedBooking.getAccommodation().getLocation(),
                savedBooking.getCheckInDate(),
                savedBooking.getCheckOutDate()
        );
        notificationService.sendMessage(user.getUsername(), message);

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
        Long accommodationId = request.getAccommodationId();
        LocalDate newCheckInDate = request.getCheckInDate();
        LocalDate newCheckOutDate = request.getCheckOutDate();
        if (newCheckInDate == null || newCheckOutDate == null) {
            throw new IllegalArgumentException(
                    "Check-in and check-out dates cannot be null.");
        }
        Booking booking = bookingRepository.findById(id).orElseThrow(() ->
                new BookingNotFoundException(id));

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

        if (booking.getAccommodation() == null) {
            throw new IllegalStateException("Booking must have an accommodation");
        }

        if (booking.getStatus() == Booking.BookingStatus.valueOf("CANCELED")) {
            throw new BookingAlreadyCancelledException("This booking has already been canceled.");
        }
        booking.setStatus(Booking.BookingStatus.valueOf("CANCELED"));
        bookingRepository.save(booking);

        String message = String.format("Booking canceled:\nUser: "
                        + "%s\nAccommodation: %s\nCheck-in: %s\nCheck-out: %s",
                booking.getUser().getUsername(),
                booking.getAccommodation().getLocation(),
                booking.getCheckInDate(),
                booking.getCheckOutDate());
        notificationService.sendMessage(booking.getUser().getUsername(), message);
    }

    @Scheduled(cron = "0 22 12 * * ?")
    @Transactional
    public void checkExpiredBookings() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        List<Booking> expiredBookings = bookingRepository
                .findNonCancelledBookingsBeforeReturnDate(tomorrow);

        if (expiredBookings.isEmpty()) {
            notificationService.sendMessage("Telegram", "No expired bookings today!");
        } else {
            for (Booking booking : expiredBookings) {
                booking.setStatus(Booking.BookingStatus.EXPIRED);
                bookingRepository.save(booking);

                Accommodation accommodation = booking.getAccommodation();
                String accommodationDetails = generateAccommodationDetails(accommodation);

                String message = String.format(
                        "Booking expired:\nUser: %s\nAccommodation: "
                                + "%s\nCheck-in: %s\nCheck-out: %s\nDetails: %s",
                        booking.getUser().getUsername(),
                        accommodation.getLocation(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        accommodationDetails
                );

                notificationService.sendMessage("Telegram", message);
            }
        }
    }

    private String generateAccommodationDetails(Accommodation accommodation) {
        return String.format("Accommodation Type: %s\nLocation: %s\nPrice: %.2f\nAvailability: %d",
                accommodation.getType(),
                accommodation.getLocation(),
                accommodation.getPrice(),
                accommodation.getAvailability());
    }
}
