package com.micasend;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.micasend.MicaSend4JS.VoidCallback;

public class Connector {
    private static final String GETJSON = "/msg?getmsg=json";
    private static final String SEND = "/msg";

    public static ArrayList<Message> fetchMessages(String jsonUrl) {

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(jsonUrl + GETJSON).openConnection();
            connection.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());

            // parse JSON to list of Message
            Gson gson = new Gson();
            ArrayList<Message> messages = gson.fromJson(reader, new TypeToken<List<Message>>() {
            }.getType());

            // for (Message obj : messages) {
            // System.out.println(obj);
            // }
            return messages;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void sendMessage(String jsonUrl, String username, String message, VoidCallback callback) {
        try {
            Map<String, String> params = Map.of(
                    "message", message,
                    "sender", username);
            String formData = params.entrySet().stream()
                    .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "="
                            + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));

            HttpClient client = HttpClient.newHttpClient();

            // Create the POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(jsonUrl + SEND))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            // Send the request and handle the response
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(response -> {
                        callback.execute();
                        // System.out.println("Response: " + response)
                    })
                    .exceptionally(e -> {
                        System.err.println("Error: " + e.getMessage());
                        callback.execute();
                        return null;
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
