package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.booking.BookingDto;
import mate.academy.dto.booking.CreateBookingRequestDto;
import mate.academy.exception.RegistrationException;
import mate.academy.service.BookingService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Booking management", description = "Endpoints for managing booking")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/my")
    @Operation(summary = "Get all booking", description = "Get a list of all available bookings")
    public List findAll(Pageable pageable) {
        return bookingService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public BookingDto getBookById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody @Valid CreateBookingRequestDto request)
            throws RegistrationException {
        return bookingService.createBooking(request);
    }

    @GetMapping
    @Operation(summary = "Get bookings by user ID and status", description =
            "Retrieves bookings based on user ID and status (Available for managers)")
    public List<BookingDto> getBookingsByUserIdAndStatus(
            @RequestParam Long userId,
            @RequestParam String status,
            Pageable pageable) {
        return bookingService.getBookingsByUserIdAndStatus(userId, status, pageable);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update booking details",
            description = "Allows users to update their booking details")
    public BookingDto updateBooking(@PathVariable Long id,
                                    @RequestBody @Valid CreateBookingRequestDto request) {
        return bookingService.updateBooking(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cancel booking", description = "Enables the cancellation of a booking")
    public void cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
    }
}
