package mate.academy.dto.booking;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateBookingRequestDto {

    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long accommodationId;
    private Long userId;
    private BookingStatus status;

    public enum BookingStatus {
        PENDING,
        CONFIRMED,
        CANCELED,
        EXPIRED
    }
}
