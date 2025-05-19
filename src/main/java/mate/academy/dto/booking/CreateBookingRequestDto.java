package mate.academy.dto.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CreateBookingRequestDto {

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    private LocalDate checkOutDate;

    @NotNull(message = "Accommodation ID is required")
    private Long accommodationId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Booking status is required")
    private BookingStatus status;

    public enum BookingStatus {
        PENDING,
        CONFIRMED,
        CANCELED,
        EXPIRED
    }
}
