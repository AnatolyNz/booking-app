package mate.academy.dto.payment;

import lombok.Data;

@Data
public class PaymentRequestDto {
    private Long bookingId;
    private String customerName;
    private String customerEmail;
    private String currency;
}
