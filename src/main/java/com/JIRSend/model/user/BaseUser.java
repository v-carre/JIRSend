package com.JIRSend.model.user;

import java.util.ArrayList;
import java.util.HashMap;

import com.JIRSend.controller.MainController;
import com.JIRSend.model.Message;
import com.JIRSend.model.db.LocalDatabase.DatabaseMessage;

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

    public static final String youString = "you";
    public static final String senderString = "sender";
    public static final String recipientString = "sender";

    protected BaseUser(MainController controller, String username, userType type) {
        this.controller = controller;
        this.username = username;
        this.type = type;
        this.ipToConversations = new HashMap<>();
        this.currentConversationName = null;
        this.currentConversationIP = null;
        retrieveConversationsFromDB();

        MainController.messageReceived.subscribe((msg) -> {
            String senderIp = controller.getIPFromUsername(msg.sender);
            if (senderIp != null)
                addToConversation(senderIp, new Message(senderString, youString, msg.message));
        });
        MainController.sendMessage.subscribe((msg) -> {
            String recipientIp = controller.getIPFromUsername(msg.receiver);
            if (recipientIp != null)
                addToConversation(recipientIp, new Message(youString, recipientString, msg.message));
        });
        MainController.contactsChange.subscribe((ch) -> {
            if (controller.getIPFromUsername(currentConversationName) == null && currentConversationIP != null)
                currentConversationName = controller.getUsernameFromIP(currentConversationIP);
        });
    }

    private void retrieveConversationsFromDB() {
        ArrayList<DatabaseMessage> msgs = controller.getAllMessagesFromDB();
        for (DatabaseMessage msg : msgs) {
            addToConversation(msg.id, new Message(msg.isMe ? youString : senderString, msg.isMe ? recipientString : youString, msg.message), msg.isRead);
        }
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

    public int getTotalUnread() {
        int totalUnread = 0;
        for (Conversation conv : this.ipToConversations.values()) {
            totalUnread += conv.numberUnRead();
        }
        return totalUnread;
    }

    public Conversation getConversation(String ip) {
        if (!ipToConversations.containsKey(ip))
            this.ipToConversations.put(ip, new Conversation());
        return this.ipToConversations.get(ip);
    }

    public void markConversationRead(String ip) {
        if (ip == null || this.ipToConversations.get(ip) == null)
            return;
        this.ipToConversations.get(ip).setUnread(0);
    }

    public int getConversationUnreadNb(String ip) {
        if (ip == null || this.ipToConversations.get(ip) == null)
            return 0;
        return this.ipToConversations.get(ip).numberUnRead();
    }

    public void addToConversation(String ip, Message msg, boolean read) {
        if (ipToConversations.containsKey(ip))
            ipToConversations.get(ip).putMessage(msg);
        else {
            ipToConversations.put(ip, new Conversation(msg));
        }
        if (!read)
            ipToConversations.get(ip).incrUnread();
    }

    public void addToConversation(String ip, Message msg) {
        addToConversation(ip, msg, false);
    }

    public String getCurrentConversationName() {
        return currentConversationName;
    }

    public void setCurrentConversationName(String convName) {
        this.currentConversationName = convName;
        this.currentConversationIP = controller.getIPFromUsername(convName);
    }
}
