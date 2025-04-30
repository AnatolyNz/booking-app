package mate.academy.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.stripe.model.checkout.Session;
import mate.academy.exception.StripeSessionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class StripeServiceTest {

    private StripeService stripeService;

    @BeforeEach
    void setUp() {
        stripeService = new StripeService();
        ReflectionTestUtils.setField(stripeService, "stripeSecretKey", "sk_test_123");
        stripeService.init();
    }

    @Test
    @DisplayName("Should return session when session ID is valid")
    void retrieveSession_WithValidId_ShouldReturnSession() throws Exception {
        String sessionId = "sess_123";
        Session mockSession = mock(Session.class);

        try (MockedStatic<Session> sessionMockedStatic = mockStatic(Session.class)) {
            sessionMockedStatic.when(() -> Session.retrieve(sessionId))
                    .thenReturn(mockSession);

            Session result = stripeService.retrieveSession(sessionId);

            assertEquals(mockSession, result);
            sessionMockedStatic.verify(() -> Session.retrieve(sessionId), times(1));
        }
    }

    @Test
    @DisplayName("Should throw StripeSessionException on failure")
    void retrieveSession_WhenStripeThrows_ShouldThrowCustomException() throws Exception {
        String sessionId = "invalid_sess";

        try (MockedStatic<Session> sessionMockedStatic = mockStatic(Session.class)) {
            sessionMockedStatic.when(() -> Session.retrieve(sessionId))
                    .thenThrow(new StripeSessionException(
                            "Failed to retrieve Stripe session: invalid_sess"));

            StripeSessionException ex = assertThrows(
                    StripeSessionException.class,
                    () -> stripeService.retrieveSession(sessionId)
            );

            assertTrue(ex.getMessage().contains("Failed to retrieve Stripe session: " + sessionId));
        }
    }
}
