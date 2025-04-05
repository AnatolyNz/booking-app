package mate.academy.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import mate.academy.model.Booking;
import mate.academy.model.Payment;
import mate.academy.repository.PaymentRepository;
import mate.academy.repository.booking.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public Payment initiatePayment(Long bookingId,
                                   String sessionUrl, String sessionId,
                                   BigDecimal amountToPay) {
        Booking booking = bookingRepository
                .findById(bookingId).orElseThrow(() ->
                        new RuntimeException("Booking not found"));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setSessionUrl(sessionUrl);
        payment.setSessionId(sessionId);
        payment.setAmountToPay(amountToPay);

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment updatePaymentStatus(Long paymentId, Payment.PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(status);
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    public Payment getPaymentBySessionId(String sessionId) {
        return paymentRepository.findBySessionId(sessionId);
    }

    @Override
    public Map<String, Object> getPaymentInformation(Long userId) {
        List<Payment> payments = paymentRepository.findByBookingId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("payments", payments);
        return response;
    }

    @Override
    public String createPaymentSession(Map<String, Object> bookingDetails) {
        String sessionUrl = "stripe_session_url";
        String sessionId = "stripe_session_id";
        BigDecimal amountToPay = new BigDecimal("100.00");

        Payment payment = initiatePayment(
                (Long) bookingDetails.get("bookingId"),
                sessionUrl, sessionId,
                amountToPay
        );
        return "Payment session created: " + payment.getSessionId();
    }

    @Override
    public String handlePaymentSuccess(String sessionId) {
        Payment payment = getPaymentBySessionId(sessionId);
        updatePaymentStatus(payment.getId(), Payment.PaymentStatus.PAID);
        return "Payment success for session: " + sessionId;
    }

    @Override
    public String handlePaymentCancel(String sessionId) {
        Payment payment = getPaymentBySessionId(sessionId);
        updatePaymentStatus(payment.getId(), Payment.PaymentStatus.CANCELLED);
        return "Payment cancelled for session: " + sessionId;
    }
}
