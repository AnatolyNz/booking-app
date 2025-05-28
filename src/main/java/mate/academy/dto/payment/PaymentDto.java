package mate.academy.dto.payment;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class PaymentDto {
    private Long id;
    private Long bookingId;
    private Long userId;
    private BigDecimal amountToPay;
    private String sessionUrl;
    private PaymentStatus status;

    public enum PaymentStatus {
        PENDING,
        PAID,
        FAILED,
        CANCELLED,
        EXPIRED
    }
}
