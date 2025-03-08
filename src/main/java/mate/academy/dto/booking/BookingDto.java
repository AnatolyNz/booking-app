package mate.academy.dto.booking;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingDto {

    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Long accommodationId;  // Instead of full accommodation, just the ID
    private Long userId;  // Instead of full user, just the ID
    private BookingStatus status;

    public enum BookingStatus {
        PENDING,
        CONFIRMED,
        CANCELED,
        EXPIRED
    }
}
