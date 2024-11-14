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
    protected String currentConversationIP;

    protected BaseUser(MainController controller, String username, userType type) {
        this.controller = controller;
        this.username = username;
        this.type = type;
        this.ipToConversations = new HashMap<>();
        this.currentConversationName = null;
        this.currentConversationIP = null;

        MainController.messageReceived.subscribe((msg) -> {
            String senderIp = controller.getIPFromUsername(msg.sender);
            if (senderIp != null)
                addToConversation(senderIp, new Message("sender", "you", msg.message));
        });
        MainController.sendMessage.subscribe((msg) -> {
            String recipientIp = controller.getIPFromUsername(msg.receiver);
            if (recipientIp != null)
                addToConversation(recipientIp, new Message("you", "recipient", msg.message));
        });
        MainController.contactsChange.subscribe((ch) -> {
            if (controller.getIPFromUsername(currentConversationName) == null && currentConversationIP != null)
                currentConversationName = controller.getUsernameFromIP(currentConversationIP);
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
            this.ipToConversations.put(ip, new Conversation());
        return this.ipToConversations.get(ip);
    }

    public void markConversationRead(String ip) {
        if (ip == null || this.ipToConversations.get(ip) == null) return;
        this.ipToConversations.get(ip).setUnread(0);
    }

    public int getConversationUnreadNb(String ip) {
        if (ip == null) return 0;
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
        this.currentConversationIP = controller.getIPFromUsername(convName);
    }
}
