package mate.academy.service;

import com.stripe.exception.StripeException;
import java.math.BigDecimal;
import java.util.Map;
import mate.academy.dto.payment.PaymentCancelResponseDto;
import mate.academy.dto.payment.PaymentDto;
import mate.academy.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    Payment initiatePayment(Long bookingId, String sessionUrl,
                            BigDecimal amountToPay);

    Payment updatePaymentStatus(Long paymentId, Payment.PaymentStatus status);

    Page<PaymentDto> getPaymentsByUserId(Long userId, Pageable pageable);

    Page<PaymentDto> getAllPayments(Pageable pageable);

    Payment getPaymentBySessionId(String sessionId);

    Payment createAndReturnPaymentSession(Map<String, Object> bookingDetails,
                                String successUrl, String cancelUrl);

    String handlePaymentSuccess(String sessionId);

    PaymentCancelResponseDto handlePaymentCancel(String sessionId);

    Payment renewPaymentSession(Long paymentId, String successUrl, String cancelUrl)
            throws StripeException;
}
