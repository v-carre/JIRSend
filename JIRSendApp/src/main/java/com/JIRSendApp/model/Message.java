package com.JIRSendApp.model;

public class Message {
    public final String sender;
    public final String receiver;
    public final String message;
    public final String time;
    public final boolean modMessage;

    public Message(String sender, String receiver, String message, String time, boolean modMessage) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.time = time;
        this.modMessage = modMessage;
    }

    public Message(String sender, String receiver, String message, String time) {
        this(sender, receiver, message, time, false);
    }
}
