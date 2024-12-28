package com.JIRSend.model;

public class Message {
    public final String sender;
    // public final String senderIP;
    public final String receiver;
    // public final String receiverIP;
    public final String message;
    public final String time;

    public Message(String sender, String receiver, String message, String time) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.time = time;
    }
}
