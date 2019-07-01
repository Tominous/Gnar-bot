package xyz.gnarbot.gnar.utils;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class HttpUtils {
    public static final OkHttpClient CLIENT = new OkHttpClient.Builder().build();

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType TEXT = MediaType.parse("text/plain; charset=utf-8");

    public static final Callback EMPTY_CALLBACK = new Callback() {
        @Override
        public void onFailure(@NotNull Call call, @NotNull IOException e) {
            call.cancel();
        }

        @Override
        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            response.close();
        }
    };
}
