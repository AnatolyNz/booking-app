package mate.academy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.dto.booking.BookingDto;
import mate.academy.service.BookingService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Booking management", description = "Endpoints for managing booking")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    @Operation(summary = "Get all booking", description = "Get a list of all available bookings")
    public List findAll(Pageable pageable) {
        return bookingService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public BookingDto getBookById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }
}
