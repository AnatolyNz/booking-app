package mate.academy.dto.payment;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {
    private String sessionId;
    private String sessionUrl;
    private Long bookingId;
    private BigDecimal amountToPay;
    private String status;
}
