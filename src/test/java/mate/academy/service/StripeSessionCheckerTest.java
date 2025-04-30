package mate.academy.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stripe.model.checkout.Session;
import java.time.Instant;
import java.util.List;
import mate.academy.exception.StripeSessionException;
import mate.academy.model.Payment;
import mate.academy.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StripeSessionCheckerTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private StripeService stripeService;

    @InjectMocks
    private StripeSessionChecker sessionChecker;

    private Payment pendingPayment;

    @BeforeEach
    void setUp() {
        pendingPayment = new Payment();
        pendingPayment.setId(1L);
        pendingPayment.setSessionId("sess_123");
        pendingPayment.setStatus(Payment.PaymentStatus.PENDING);
    }

    @Test
    void checkPendingSessions_ShouldMarkExpired_WhenSessionExpired() throws Exception {
        // Given
        Session session = mock(Session.class);
        long expiredTimestamp = Instant.now().minusSeconds(10).getEpochSecond(); // Already expired
        when(session.getExpiresAt()).thenReturn(expiredTimestamp);

        when(paymentRepository.findAllByStatus(Payment.PaymentStatus.PENDING))
                .thenReturn(List.of(pendingPayment));

        when(stripeService.retrieveSession("sess_123")).thenReturn(session);

        // When
        sessionChecker.checkPendingSessions();

        // Then
        verify(paymentRepository, times(1)).save(argThat(payment ->
                payment.getStatus() == Payment.PaymentStatus.EXPIRED));
    }

    @Test
    void checkPendingSessions_ShouldSkip_WhenSessionNotExpired() throws Exception {
        // Given
        Session session = mock(Session.class);
        long futureTimestamp = Instant.now().plusSeconds(3600).getEpochSecond(); // Not expired
        when(session.getExpiresAt()).thenReturn(futureTimestamp);

        when(paymentRepository.findAllByStatus(Payment.PaymentStatus.PENDING))
                .thenReturn(List.of(pendingPayment));

        when(stripeService.retrieveSession("sess_123")).thenReturn(session);

        // When
        sessionChecker.checkPendingSessions();

        // Then
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void checkPendingSessions_ShouldHandleStripeException() throws Exception {
        // Given
        when(paymentRepository.findAllByStatus(Payment.PaymentStatus.PENDING))
                .thenReturn(List.of(pendingPayment));

        when(stripeService.retrieveSession("sess_123"))
                .thenThrow(new StripeSessionException("Session error"));

        // When
        sessionChecker.checkPendingSessions();

        // Then
        verify(paymentRepository, never()).save(any());
        // Optionally verify logging or print statement
    }
}
