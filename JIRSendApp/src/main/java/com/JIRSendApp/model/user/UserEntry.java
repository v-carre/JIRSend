package com.JIRSendApp.model.user;

public class UserEntry {
    public boolean online;
    public String username;

    public UserEntry(boolean online, String username) {
        this.online = online;
        this.username = username;
    }

    @Override
    public String toString() {
        return username + (online ? "(online)" : "(offline)");
    }
}