package com.JIRSend.model.user;

import java.util.ArrayList;

import com.JIRSend.model.Message;

public class Conversation {
    public ArrayList<Message> messages;
    ;
    public Conversation() {
        this.messages = new ArrayList<>();
    }

    public Conversation(Message message) {
        this();
        this.messages.add(message);
    }

    public Conversation(ArrayList<Message> messages) {
        this();
        this.messages.addAll(messages);
    }
}
