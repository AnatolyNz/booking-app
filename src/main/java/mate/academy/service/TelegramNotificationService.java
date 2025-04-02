package mate.academy.service;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TelegramNotificationService implements NotificationService {

    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");
    private static final String CHAT_ID = System.getenv("CHAT_ID");
    private static final String API_URL = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";

    private final OkHttpClient client;

    public TelegramNotificationService() {
        this.client = new OkHttpClient();
    }

    @Override
    public void sendMessage(String recipient, String message) {
        // Create the request body with the parameters
        RequestBody body = new FormBody.Builder()
                .add("chat_id", CHAT_ID)
                .add("text", message)
                .build();

        // Create the HTTP request
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    System.out.println("Message sent successfully!");
                } else {
                    System.out.println("Failed to send message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Request failed: " + e.getMessage());
            }
        });
    }
}

