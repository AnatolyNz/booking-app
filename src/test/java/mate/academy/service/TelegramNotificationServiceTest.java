package mate.academy.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TelegramNotificationServiceTest {

    @Mock
    private OkHttpClient httpClient;

    private TelegramNotificationService notificationService;

    private final String botToken = "dummy-bot-token";
    private final String chatId = "dummy-chat-id";

    @BeforeEach
    void setUp() {
        // Create the service with mock and test values
        notificationService = new TelegramNotificationService(httpClient, botToken, chatId);
    }

    @Test
    @DisplayName("Should call Telegram API with correct request")
    void sendMessage_ShouldCallTelegramApi() {
        String message = "Test message";

        Call mockCall = mock(Call.class);
        when(httpClient.newCall(any(Request.class))).thenReturn(mockCall);

        notificationService.sendMessage("ignored", message);

        verify(httpClient, times(1)).newCall(any(Request.class));
    }

    @Test
    @DisplayName("Should handle HTTP failure gracefully")
    void sendMessage_ShouldHandleFailure() {
        String message = "Failure test";
        Call mockCall = mock(Call.class);

        // Correct way to mock a void method
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(0);
            callback.onFailure(mockCall, new IOException("Simulated failure"));
            return null;
        }).when(mockCall).enqueue(any());

        when(httpClient.newCall(any(Request.class))).thenReturn(mockCall);

        notificationService.sendMessage("ignored", message);

        verify(httpClient, times(1)).newCall(any(Request.class));
    }
}
