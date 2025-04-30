package mate.academy.service;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TelegramNotificationService implements NotificationService {

    private final OkHttpClient client;
    private final String botToken;
    private final String chatId;

    public TelegramNotificationService(OkHttpClient client,
                                       @Value("${BOT_TOKEN}") String botToken,
                                       @Value("${CHAT_ID}") String chatId) {
        this.client = client;
        this.botToken = botToken;
        this.chatId = chatId;
    }

    @Override
    public void sendMessage(String recipient, String message) {
        RequestBody body = new FormBody.Builder()
                .add("chat_id", chatId)
                .add("text", message)
                .add("parse_mode", "Markdown")
                .build();

        String apiUrl = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("Message sent successfully!");
            }

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Request failed: " + e.getMessage());
            }
        });
    }
}
