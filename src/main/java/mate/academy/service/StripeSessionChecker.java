package mate.academy.service;

import com.stripe.model.checkout.Session;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.exception.StripeSessionException;
import mate.academy.model.Payment;
import mate.academy.repository.PaymentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeSessionChecker {

    private final PaymentRepository paymentRepository;
    private final StripeService stripeService;

    @Scheduled(fixedRate = 60000) // every 60 seconds
    public void checkPendingSessions() {
        List<Payment> pendingPayments = paymentRepository
                .findAllByStatus(Payment.PaymentStatus.PENDING);

        for (Payment payment : pendingPayments) {
            try {
                Session session = stripeService.retrieveSession(payment.getSessionId());

                if (session.getExpiresAt() != null
                        && Instant.ofEpochSecond(session.getExpiresAt())
                        .isBefore(Instant.now())) {

                    payment.setStatus(Payment.PaymentStatus.EXPIRED);
                    paymentRepository.save(payment);
                }

            } catch (StripeSessionException e) {
                System.err.println("Stripe session check failed: " + e.getMessage());
            }
        }
    }
}
