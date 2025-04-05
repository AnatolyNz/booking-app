package mate.academy.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import mate.academy.model.Payment;

public interface PaymentService {

    Payment initiatePayment(Long bookingId, String sessionUrl, String sessionId,
                            BigDecimal amountToPay);

    Payment updatePaymentStatus(Long paymentId, Payment.PaymentStatus status);

    List<Payment> getPaymentsByBooking(Long bookingId);

    Payment getPaymentBySessionId(String sessionId);

    Map<String, Object> getPaymentInformation(Long userId);

    String createPaymentSession(Map<String, Object> bookingDetails);

    String handlePaymentSuccess(String sessionId);

    String handlePaymentCancel(String sessionId);
}

