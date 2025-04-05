package mate.academy.repository;

import java.util.List;
import mate.academy.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByStatus(Payment.PaymentStatus status);

    List<Payment> findByBookingId(Long bookingId);

    Payment findBySessionId(String sessionId);
}
