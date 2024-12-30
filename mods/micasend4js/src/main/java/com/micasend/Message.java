package com.micasend;

public class Message {
    public String id, content, sender, date_time, id_certified_user, rank;

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + "'}";
    }

    public int id() {
        try {
            return Integer.parseInt(id);
        } catch (Exception e) {
            return -1;
        }
    }
}
