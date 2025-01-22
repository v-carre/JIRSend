package com.JIRSendApp.model.user;

import java.util.ArrayList;

import com.JIRSendApp.model.Message;

public class Conversation {
    private ArrayList<Message> messages;
    private int unRead = 0;
    public Conversation() {
        this.unRead = 0;
        this.messages = new ArrayList<>();
    }

    public synchronized int numberUnRead() {
        return unRead;
    }

    public synchronized void setUnread(int unread) {
        this.unRead = unread;
    }

    public synchronized  void incrUnread() {
        this.unRead++;
    }

    public synchronized ArrayList<Message> getMessages() {
        return this.messages;
    }

    public synchronized void putMessage(Message msg) {
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
