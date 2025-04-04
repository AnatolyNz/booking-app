package mate.academy.repository.booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import mate.academy.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long>,
        JpaSpecificationExecutor<Booking> {

    Optional<Booking> getBookingById(Long id);

    Page<Booking> findAllByUserId(Long userId, Pageable pageable);

    List<Booking> findAll();

    Page<Booking> findByUserIdAndStatus(Long userId,
                                        Booking.BookingStatus status, Pageable pageable);

    boolean existsByAccommodationIdAndCheckInDateBeforeAndCheckOutDateAfter(
            Long accommodationId, LocalDate checkInDateBefore, LocalDate checkOutDateAfter);

    @Query("SELECT b FROM Booking b WHERE b.status != 'CANCELED' AND b.checkOutDate <= :date")
    List<Booking> findNonCancelledBookingsBeforeReturnDate(@Param("date") LocalDate date);
}
