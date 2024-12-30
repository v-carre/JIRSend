package com.JIRSendApp.model.user;

public class UserEntry {
    public static enum Status { Offline, Busy, Away, Online };
    public Status online;
    public String username;

    public UserEntry(Status online, String username) {
        this.online = online;
        this.username = username;
    }

    @Override
    public String toString() {
        return username + (online + "(" + online + ")");
    }

    public boolean online() {
        return online != Status.Offline;
    }
}