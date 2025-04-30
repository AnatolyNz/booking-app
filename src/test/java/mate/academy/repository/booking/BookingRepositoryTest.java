package mate.academy.repository.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import mate.academy.model.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootTest
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @BeforeEach
    void beforeEach() {
        executeScripts(
                "database/bookings/remove-bookings.sql",
                "database/bookings/remove-accommodations.sql",
                "database/bookings/remove-users.sql",
                "database/bookings/add-users.sql",
                "database/bookings/add-accommodations.sql",
                "database/bookings/add-bookings.sql"
        );
    }

    private void executeScripts(String... scripts) {
        try (Connection connection = dataSource.getConnection()) {
            for (String script : scripts) {
                ScriptUtils.executeSqlScript(connection, new ClassPathResource(script));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Get booking by valid ID")
    void getBookingById_WithValidId_ShouldReturnBooking() {
        Optional<Booking> optionalBooking = bookingRepository.getBookingById(1L);
        assertTrue(optionalBooking.isPresent(), "Booking with ID 1 should be present");
    }

    @Test
    @DisplayName("Find all by user ID with pagination")
    void findAllByUserId_WithValidUserId_ShouldReturnPaginatedBookings() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Booking> bookings = bookingRepository.findAllByUserId(1L, pageable);
        assertEquals(2, bookings.getTotalElements());
    }

    @Test
    @DisplayName("Find all bookings")
    void findAll_ShouldReturnAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        assertEquals(3, bookings.size());
    }

    @Test
    @DisplayName("Find bookings by user ID and status")
    void findByUserIdAndStatus_ShouldReturnFilteredBookings() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Booking> bookings = bookingRepository
                .findByUserIdAndStatus(1L,
                        Booking.BookingStatus.CONFIRMED, pageable);
        assertEquals(1, bookings.getTotalElements());
    }

    @Test
    @DisplayName("Check overlapping booking exists")
    void existsByAccommodationIdAndDates_ShouldReturnTrueIfOverlapping() {
        boolean exists = bookingRepository
                .existsByAccommodationIdAndCheckInDateBeforeAndCheckOutDateAfter(
                1L, LocalDate.of(2025,
                        5, 7), LocalDate.of(2025,
                        5, 9));
        assertTrue(exists);
    }

    @Test
    @DisplayName("Find non-canceled bookings before return date")
    void findNonCancelledBookingsBeforeReturnDate_ShouldReturnValidBookings() {
        LocalDate date = LocalDate.of(2025, 5, 10);
        List<Booking> bookings = bookingRepository.findNonCancelledBookingsBeforeReturnDate(date);
        assertFalse(bookings.isEmpty());
    }
}
