package mate.academy.repository;

import java.util.List;
import mate.academy.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Payment findBySessionId(String sessionId);

    @Query("SELECT p FROM Payment p WHERE p.isDeleted = false")
    List<Payment> findAllPayments(Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.booking.user.id = :userId")
    Page<Payment> findAllByUserId(@Param("userId") Long userId, Pageable pageable);
}
