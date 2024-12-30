package com.JIRSendApp.model.user;

import javax.swing.ImageIcon;

public class UserEntry {
    public static enum Status { Offline, Busy, Away, Online };
    public Status online;
    public String username;
    public ImageIcon icon;

    public UserEntry(Status online, String username, ImageIcon icon) {
        this.online = online;
        this.username = username;
        this.icon = icon;
    }

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