package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import mate.academy.mapper.PaymentMapper;
import mate.academy.model.Booking;
import mate.academy.model.Payment;
import mate.academy.repository.PaymentRepository;
import mate.academy.repository.booking.BookingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private StripeCheckoutService stripeCheckoutService;

    @Mock
    private TelegramNotificationService telegramNotificationService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("Should initiate payment when booking exists")
    void initiatePayment_WithValidBooking_ShouldCreatePayment() {
        Long bookingId = 1L;
        Booking booking = new Booking();
        booking.setId(bookingId);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BigDecimal amountToPay = new BigDecimal("100.00");
        Payment savedPayment = new Payment();
        savedPayment.setBooking(booking);
        String sessionUrl = "http://checkout.stripe.com/session123";
        savedPayment.setSessionUrl(sessionUrl);
        savedPayment.setAmountToPay(amountToPay);

        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        Payment result = paymentService.initiatePayment(bookingId, sessionUrl, amountToPay);

        assertNotNull(result);
        assertEquals(sessionUrl, result.getSessionUrl());
        assertEquals(amountToPay, result.getAmountToPay());
    }

    @Test
    @DisplayName("Should throw exception if booking not found")
    void initiatePayment_WithInvalidBooking_ShouldThrowException() {
        Long bookingId = 999L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                paymentService.initiatePayment(bookingId, "url", BigDecimal.TEN));
    }

    @Test
    @DisplayName("Should return payment by sessionId")
    void getPaymentBySessionId_WithValidId_ShouldReturnPayment() {
        String sessionId = "session-123";
        Payment payment = new Payment();
        payment.setSessionId(sessionId);

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(payment);

        Payment result = paymentService.getPaymentBySessionId(sessionId);

        assertNotNull(result);
        assertEquals(sessionId, result.getSessionId());
    }

    @Test
    @DisplayName("Should throw exception if payment with session ID not found")
    void getPaymentBySessionId_WithInvalidId_ShouldThrowException() {
        String sessionId = "invalid-session";

        when(paymentRepository.findBySessionId(sessionId)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () ->
                paymentService.getPaymentBySessionId(sessionId));

        assertTrue(exception.getMessage().contains("Payment not found with session ID"));
    }
}
