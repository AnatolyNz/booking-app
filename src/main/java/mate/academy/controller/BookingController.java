package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.booking.BookingDto;
import mate.academy.dto.booking.CreateBookingRequestDto;
import mate.academy.exception.RegistrationException;
import mate.academy.model.Booking;
import mate.academy.model.User;
import mate.academy.service.BookingService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Booking management", description = "Endpoints for managing booking")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @GetMapping("/my")
    @Operation(summary = "Get all booking", description = "Get a list of all available bookings")
    public List<BookingDto> getAllBookings(Authentication authentication,
                                           Pageable pageable,
                                           @RequestParam(value = "userId", required = false) Long userId) {
        User currentUser = (User) authentication.getPrincipal();
        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) && userId != null) {
            return bookingService.getBookingsByUserId(userId, pageable);
        } else {
            return bookingService.getAllBookingsWithoutUserId(pageable);
        }
    }

    @GetMapping("/{id}")
    public BookingDto getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody @Valid CreateBookingRequestDto request)
            throws RegistrationException {
        return bookingService.createBooking(request);
    }

    @GetMapping("/{userId}/{status}")
    @Operation(summary = "Get bookings by user ID and status", description =
            "Retrieves bookings based on user ID and status (Available for managers)")
    public List<BookingDto> getBookingsByUserIdAndStatus(
            @PathVariable Long userId,
            @PathVariable Booking.BookingStatus status,
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
