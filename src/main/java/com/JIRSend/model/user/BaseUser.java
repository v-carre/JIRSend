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
    protected String currentConversationName;

    protected BaseUser(MainController controller, String username, userType type) {
        this.controller = controller;
        this.username = username;
        this.type = type;
        this.ipToConversations = new HashMap<>();
        this.currentConversationName = null;

        MainController.messageReceived.subscribe((msg) -> {
            String senderIp = controller.getIPfromUsername(msg.sender);
            if (senderIp != null)
                addToConversation(senderIp, msg);
        });
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
        if (!ipToConversations.containsKey(ip))
            return null;
        return this.ipToConversations.get(ip);
    }

    public void markConversationRead(String ip) {
        this.ipToConversations.get(ip).setUnread(0);
    }

    public int getConversationUnreadNb(String ip) {
        return this.ipToConversations.get(ip).numberUnRead();
    }

    public void addToConversation(String ip, Message msg) {
        if (ipToConversations.containsKey(ip))
            ipToConversations.get(ip).putMessage(msg);
        else {
            ipToConversations.put(ip, new Conversation(msg));
        }
        if (!currentConversationName.equals(msg.sender))
            ipToConversations.get(ip).incrUnread();
    }

    public String getCurrentConversationName() {
        return currentConversationName;
    }

    public void setCurrentConversationName(String convName) {
        this.currentConversationName = convName;
    }
}
