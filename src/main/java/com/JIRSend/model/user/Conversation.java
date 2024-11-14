package com.JIRSend.model.user;

import java.util.ArrayList;

import com.JIRSend.model.Message;

public class Conversation {
    private ArrayList<Message> messages;
    private int unRead = 0;
    public Conversation() {
        this.unRead = 0;
        this.messages = new ArrayList<>();
    }

    public int numberUnRead() {
        return unRead;
    }

    public void setUnread(int unread) {
        this.unRead = unread;
    }

    public void incrUnread() {
        this.unRead++;
    }

    public ArrayList<Message> getMessages() {
        return this.messages;
    }

    public void putMessage(Message msg) {
        this.messages.add(msg);
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
