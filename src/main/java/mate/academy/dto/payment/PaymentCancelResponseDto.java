package mate.academy.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentCancelResponseDto {
    private String message;
    private String retryUrl;
}
