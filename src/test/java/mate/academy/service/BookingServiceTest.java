package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import mate.academy.dto.booking.BookingDto;
import mate.academy.dto.booking.CreateBookingRequestDto;
import mate.academy.mapper.BookingMapper;
import mate.academy.model.Accommodation;
import mate.academy.model.Booking;
import mate.academy.model.Role;
import mate.academy.model.User;
import mate.academy.repository.PaymentRepository;
import mate.academy.repository.UserRepository;
import mate.academy.repository.accommodation.AccommodationRepository;
import mate.academy.repository.booking.BookingRepository;
import mate.academy.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private AccommodationRepository accommodationRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Verify getBookingById returns correct booking when booking exists")
    void getBookingById_WithValidId_ShouldReturnBooking() {
        Long bookingId = 1L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setCheckInDate(LocalDate.of(2025, 5, 1));
        booking.setCheckOutDate(LocalDate.of(2025, 5, 7));
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        booking.setAccommodation(new Accommodation());
        booking.setUser(new User());

        BookingDto expectedDto = new BookingDto();
        expectedDto.setId(bookingId);

        when(bookingRepository.getBookingById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(expectedDto);

        BookingDto actual = bookingService.getBookingById(bookingId);

        assertEquals(expectedDto, actual);

        verify(bookingRepository).getBookingById(bookingId);
        verify(bookingMapper).toDto(booking);
        verifyNoMoreInteractions(bookingRepository, bookingMapper);
    }

    @Test
    @DisplayName("Verify getBookingsByUserId returns list of bookings")
    void getBookingsByUserId_ShouldReturnBookings() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        List<Booking> bookings = Arrays.asList(booking1, booking2);
        Page<Booking> page = new PageImpl<>(bookings, pageable, bookings.size());

        when(bookingRepository.findAllByUserId(userId, pageable)).thenReturn(page);
        when(bookingMapper.toDto(booking1)).thenReturn(new BookingDto());
        when(bookingMapper.toDto(booking2)).thenReturn(new BookingDto());

        Page<BookingDto> result = bookingService.getBookingsByUserId(userId, pageable);

        assertEquals(2, result.getContent().size());
        assertNotNull(result.getContent().get(0));
        assertNotNull(result.getContent().get(1));
    }

    @Test
    @DisplayName("Verify createBooking creates and returns a valid "
            + "booking using dates from CreateBookingRequestDto")
    void createBooking_WithUserDatesFromDto_ShouldReturnCreatedBooking() {
        Long accommodationId = 100L;

        LocalDate checkInDate = LocalDate.now().plusDays(2);
        LocalDate checkOutDate = LocalDate.now().plusDays(5);

        CreateBookingRequestDto requestDto = new CreateBookingRequestDto();
        requestDto.setAccommodationId(accommodationId);
        requestDto.setCheckInDate(checkInDate);
        requestDto.setCheckOutDate(checkOutDate);
        requestDto.setStatus(CreateBookingRequestDto.BookingStatus.PENDING);

        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setEmail("user@example.com");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");
        User testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");
        testUser.setPassword("password");

        Role role = new Role();
        role.setRoleName(Role.RoleName.USER);
        testUser.setRole(role);

        SecurityContextHolder.setContext(
                new SecurityContextImpl(new TestingAuthenticationToken(testUser, null))
        );

        Accommodation mockAccommodation = new Accommodation();
        mockAccommodation.setId(accommodationId);
        mockAccommodation.setLocation("Test Location");
        mockAccommodation.setSize("Large");
        mockAccommodation.setAmenities(List.of("WiFi", "Pool"));
        mockAccommodation.setPrice(BigDecimal.valueOf(100));
        mockAccommodation.setAvailability(10);
        mockAccommodation.setType(Accommodation.Type.HOUSE);
        mockAccommodation.setDailyRate(BigDecimal.valueOf(120));

        Booking savedBooking = new Booking();
        savedBooking.setId(1L);
        savedBooking.setUser(mockUser);
        savedBooking.setAccommodation(mockAccommodation);
        savedBooking.setCheckInDate(checkInDate);
        savedBooking.setCheckOutDate(checkOutDate);
        savedBooking.setStatus(Booking.BookingStatus.PENDING);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(savedBooking.getId());
        bookingDto.setUserId(userId);
        bookingDto.setAccommodationId(accommodationId);
        bookingDto.setCheckInDate(checkInDate);
        bookingDto.setCheckOutDate(checkOutDate);
        bookingDto.setStatus(BookingDto.BookingStatus.PENDING);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(paymentRepository.countPendingPaymentsByUserId(userId)).thenReturn(0L);

        Booking bookingToSave = new Booking();
        bookingToSave.setUser(mockUser); // must be set
        bookingToSave.setCheckInDate(checkInDate);
        bookingToSave.setCheckOutDate(checkOutDate);
        bookingToSave.setAccommodation(mockAccommodation); // needed if notification uses it

        when(bookingMapper.toEntity(requestDto)).thenReturn(bookingToSave);
        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingToSave);

        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.createBooking(requestDto, mockUser.getId());

        assertNotNull(result);
        assertEquals(checkInDate, result.getCheckInDate());
        assertEquals(checkOutDate, result.getCheckOutDate());
        assertEquals(userId, result.getUserId());
        assertEquals(accommodationId, result.getAccommodationId());
        assertEquals(BookingDto.BookingStatus.PENDING, result.getStatus());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("Verify updateBooking updates an existing booking")
    void updateBooking_WithValidId_ShouldUpdateBooking() {
        Long id = 1L;
        CreateBookingRequestDto request = new CreateBookingRequestDto();
        Booking booking = new Booking();
        booking.setId(id);
        request.setAccommodationId(1L);
        request.setCheckInDate(LocalDate.of(2025, 5, 1));
        request.setCheckOutDate(LocalDate.of(2025, 5, 5));
        request.setStatus(CreateBookingRequestDto.BookingStatus.CONFIRMED);

        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));

        bookingService.updateBooking(id, request);

        verify(bookingRepository, times(1)).findById(id);
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    @DisplayName("Verify cancelBooking calls save with updated booking status")
    void cancelBooking_WithValidId_ShouldCallSave() {
        Long id = 1L;
        Booking booking = new Booking();
        booking.setId(id);
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        Accommodation accommodation = new Accommodation();
        accommodation.setLocation("Test Location"); // or any relevant dummy data
        booking.setAccommodation(accommodation);

        User user = new User();
        user.setEmail("user@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPassword("dummy");

        Role role = new Role();
        role.setRoleName(Role.RoleName.USER);
        user.setRole(role);

        booking.setUser(user);
        accommodation.setLocation("Test Location");
        booking.setAccommodation(accommodation);

        booking.setUser(user);

        when(bookingRepository.findById(id)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(id);

        verify(bookingRepository, times(1)).save(argThat(savedBooking ->
                savedBooking.getStatus() == Booking.BookingStatus.CANCELED
        ));
    }

    @Test
    @DisplayName("Verify getBookingsByUserIdAndStatus returns bookings with correct status")
    void getBookingsByUserIdAndStatus_ShouldReturnBookings() {
        Long userId = 1L;
        Booking.BookingStatus status = Booking.BookingStatus.CONFIRMED;
        Pageable pageable = PageRequest.of(0, 10);
        Booking booking1 = new Booking();
        booking1.setStatus(status);
        List<Booking> bookings = Arrays.asList(booking1);
        Page<Booking> page = new PageImpl<>(bookings, pageable, bookings.size());

        when(bookingRepository.findByUserIdAndStatus(userId, status, pageable)).thenReturn(page);
        when(bookingMapper.toDto(booking1)).thenReturn(new BookingDto());

        Page<BookingDto> result = bookingService.getBookingsByUserIdAndStatus(userId,
                status, pageable);

        assertEquals(1, result.getContent().size());
    }
}
