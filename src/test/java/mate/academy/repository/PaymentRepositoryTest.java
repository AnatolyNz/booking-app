package mate.academy.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import mate.academy.model.Accommodation;
import mate.academy.model.Booking;
import mate.academy.model.Payment;
import mate.academy.model.User;
import mate.academy.repository.booking.BookingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@Sql(scripts = "classpath:database/cleanup.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest
@AutoConfigureTestEntityManager
@Transactional
public class PaymentRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testFindBySessionId() {
        User user = new User();
        user.setEmail("email@example.com");
        user.setFirstName("First");
        user.setLastName("Last");
        user.setPassword("password");
        user.setRole(User.UserRole.USER);
        entityManager.persistAndFlush(user);

        Accommodation accommodation = new Accommodation();
        accommodation.setAmenities(List.of("WiFi"));
        accommodation.setType(Accommodation.Type.HOUSE);
        accommodation.setLocation("Kyiv");
        accommodation.setPrice(BigDecimal.valueOf(1000));
        accommodation.setDailyRate(BigDecimal.valueOf(100));
        accommodation.setSize("LARGE");
        accommodation.setAvailability(10);
        entityManager.persistAndFlush(accommodation);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setAccommodation(accommodation);
        booking.setCheckInDate(LocalDate.now());
        booking.setCheckOutDate(LocalDate.now().plusDays(1));
        booking.setStatus(Booking.BookingStatus.PENDING);
        entityManager.persistAndFlush(booking);

        Payment payment = new Payment();
        payment.setSessionId("session123");
        payment.setAmountToPay(new BigDecimal("100.00"));
        payment.setBooking(booking);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setSessionUrl("http://example.com/session123");
        entityManager.persistAndFlush(payment);

        Payment found = paymentRepository.findBySessionId("session123");

        assertNotNull(found);
        assertEquals("session123", found.getSessionId());
    }

    @Test
    public void testFindAllPayments() {
        User user = new User();
        user.setEmail("fan.zhen@example.com");
        user.setFirstName("Fan");
        user.setLastName("Zhen");
        user.setPassword("securepassword22");
        user.setRole(User.UserRole.USER);
        entityManager.persistAndFlush(user);

        Accommodation accommodation = new Accommodation();
        accommodation.setAmenities(Arrays.asList("Wi-Fi", "Air Conditioning"));
        accommodation.setType(Accommodation.Type.HOUSE);
        accommodation.setLocation("Kyiv");
        accommodation.setPrice(BigDecimal.valueOf(750));
        accommodation.setDailyRate(BigDecimal.valueOf(100));
        accommodation.setSize("50 sq.m");
        accommodation.setAvailability(10);
        entityManager.persistAndFlush(accommodation);

        Booking booking1 = new Booking();
        booking1.setCheckInDate(LocalDate.now());
        booking1.setCheckOutDate(LocalDate.now().plusDays(1));
        booking1.setAccommodation(accommodation);
        booking1.setUser(user);
        booking1.setStatus(Booking.BookingStatus.PENDING);
        entityManager.persistAndFlush(booking1);

        Booking booking2 = new Booking();
        booking2.setCheckInDate(LocalDate.now().plusDays(1));
        booking2.setCheckOutDate(LocalDate.now().plusDays(2));
        booking2.setAccommodation(accommodation);
        booking2.setUser(user);
        booking2.setStatus(Booking.BookingStatus.PENDING);
        entityManager.persistAndFlush(booking2);

        // Create and persist Payment entities
        Payment payment1 = new Payment();
        payment1.setSessionId("session1");
        payment1.setAmountToPay(new BigDecimal("100.00"));
        payment1.setStatus(Payment.PaymentStatus.PENDING);
        payment1.setSessionUrl("http://example.com/session1");
        payment1.setBooking(booking1); // Set the booking reference
        entityManager.persistAndFlush(payment1);

        Payment payment2 = new Payment();
        payment2.setSessionId("session2");
        payment2.setAmountToPay(new BigDecimal("200.00"));
        payment2.setStatus(Payment.PaymentStatus.PENDING);
        payment2.setSessionUrl("http://example.com/session2");
        payment2.setBooking(booking2);
        payment2.setDeleted(false);
        entityManager.persistAndFlush(payment2);

        List<Payment> payments = paymentRepository.findAllPayments(PageRequest.of(0, 10));
        assertEquals(2, payments.size());
    }
}
