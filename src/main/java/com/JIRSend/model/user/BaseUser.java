package com.JIRSend.model.user;

import java.util.HashMap;

import com.JIRSend.controller.MainController;
import com.JIRSend.model.Message;

public abstract class BaseUser {
    protected enum userType {
        User, Admin
    }

    protected MainController controller;
    protected userType type;
    protected String username;
    protected int id;
    protected HashMap<String, Conversation> ipToConversations;

    protected BaseUser(MainController controler, String username, userType type) {
        this.controller = controler;
        this.username = username;
        this.type = type;
        this.ipToConversations = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public userType getType() {
        return this.type;
    }

    public Conversation getConversation(String ip) {
        return this.ipToConversations.get(ip);
    }

    public void addToConversation(String ip, Message msg) {
        if (ipToConversations.containsKey(ip))
            ipToConversations.get(ip).messages.add(msg);
        else {
            ipToConversations.put(ip, new Conversation(msg));
        }
    }
}
