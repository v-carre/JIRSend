package com.micasend;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Connector {
    public static ArrayList<Message> fetchMessages(String jsonUrl) {

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(jsonUrl).openConnection();
            connection.setRequestMethod("GET");

            InputStreamReader reader = new InputStreamReader(connection.getInputStream());

            // parse JSON to list of Message
            Gson gson = new Gson();
            ArrayList<Message> messages = gson.fromJson(reader, new TypeToken<List<Message>>() {}.getType());

            // for (Message obj : messages) {
            //     System.out.println(obj);
            // }
            return messages;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
