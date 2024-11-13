package com.JIRSend.model;

public class Message { // struct-like class
    public final String sender;
    // public final String senderIP;
    public final String receiver;
    // public final String receiverIP;
    public final String message;

    public Message(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }
}
