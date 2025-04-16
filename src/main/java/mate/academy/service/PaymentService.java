package mate.academy.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import mate.academy.dto.payment.PaymentDto;
import mate.academy.model.Payment;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    Payment initiatePayment(Long bookingId, String sessionUrl,
                            BigDecimal amountToPay);

    Payment updatePaymentStatus(Long paymentId, Payment.PaymentStatus status);

    List<PaymentDto> getPaymentsByUserId(Long userId, Pageable pageable);

    List<PaymentDto> getAllPayments(Pageable pageable);

    Payment getPaymentBySessionId(String sessionId);

    Payment createAndReturnPaymentSession(Map<String, Object> bookingDetails,
                                String successUrl, String cancelUrl);

    String handlePaymentSuccess(String sessionId);

    String handlePaymentCancel(String sessionId);
}
