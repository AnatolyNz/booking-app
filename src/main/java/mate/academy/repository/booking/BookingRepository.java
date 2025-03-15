package mate.academy.repository.booking;

import java.util.List;
import java.util.Optional;
import mate.academy.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookingRepository extends JpaRepository<Booking, Long>,
        JpaSpecificationExecutor<Booking> {

    Optional<Booking> getBookingById(Long id);

    List<Booking> findAll();

    Page<Booking> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
}
