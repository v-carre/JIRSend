package com.JIRSendAPI;

import com.JIRSendAPI.JIRSendMod.JIRSendModInformation;

public class ModUser {
    public static enum Status { Offline, Busy, Away, Online };
    public final JIRSendModInformation mod;
    public final String userID;
    public final String username;
    public final boolean updateUsername;
    public final Status online;

    public ModUser(JIRSendModInformation info, String userID, String username, Status online, boolean updateUsername) {
        this.updateUsername = updateUsername;
        this.mod = info;
        this.userID = userID;
        this.username = username;
        this.online = online;
    }

    public ModUser(JIRSendModInformation info, String userID, String username, Status online) {
        this(info, userID, username, online, true);
    }

    public boolean online() {
        return online != Status.Offline;
    }
}
