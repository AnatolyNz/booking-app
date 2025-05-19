package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.booking.BookingDto;
import mate.academy.dto.booking.CreateBookingRequestDto;
import mate.academy.model.Booking;
import mate.academy.model.Role;
import mate.academy.model.User;
import mate.academy.repository.UserRepository;
import mate.academy.service.BookingService;
import mate.academy.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Booking management", description = "Endpoints for managing booking")
@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final UserService userService;

    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    @GetMapping("/my")
    @Operation(summary = "Get all bookings", description = "Get a list of all available bookings")
    public List<BookingDto> getAllBookings(Authentication authentication, Pageable pageable) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email));

        boolean isAdmin = user.getRole().getRoleName() == Role.RoleName.ADMIN;

        if (isAdmin) {
            return bookingService.getAllBookingsWithoutUserId(pageable);
        } else {
            return bookingService.getBookingsByUserId(user.getId(), pageable);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID", description
            = "Retrieve a booking by its unique identifier.")
    public BookingDto getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new booking", description
            = "Creates a booking for the authenticated user.")
    public BookingDto createBooking(@RequestBody @Valid CreateBookingRequestDto request,
                                    Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        String email = user.getEmail();

        try {
            return bookingService.createBooking(request, user.getId());
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
        }
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
