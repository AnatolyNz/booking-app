package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class StripeCheckoutServiceTest {

    @InjectMocks
    private StripeCheckoutService stripeCheckoutService;

    @BeforeEach
    void setUp() {
        // Inject test value for private field annotated with @Value
        ReflectionTestUtils.setField(stripeCheckoutService, "stripeSecretKey", "sk_test_123456");
        stripeCheckoutService.init(); // set Stripe.apiKey
    }

    @Test
    @DisplayName("Should create checkout session with valid input")
    void createCheckoutSession_WithValidData_ShouldReturnSession() throws StripeException {
        // Mocked session to return
        Session mockSession = mock(Session.class);

        // Mock Stripe's static Session.create method
        try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
            mockedSession.when(() -> Session.create(any(SessionCreateParams.class)))
                    .thenReturn(mockSession);

            // When
            Session result = stripeCheckoutService.createCheckoutSession(
                    "https://success.com",
                    "https://cancel.com",
                    2000L,
                    "usd",
                    "booking-789"
            );

            // Then
            assertEquals(mockSession, result);
            mockedSession.verify(() -> Session.create(any(SessionCreateParams.class)), times(1));
        }
    }
}
