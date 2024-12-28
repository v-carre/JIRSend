package com.JIRSend.mods;

import com.JIRSend.mods.JIRSendMod.JIRSendModInformation;

public class ModUser {
    public final JIRSendModInformation mod;
    public final String userID;
    public final String username;
    public final boolean online;

    public ModUser(JIRSendModInformation info, String userID, String username, boolean online) {
        this.mod = info;
        this.userID = userID;
        this.username = username;
        this.online = online;
    }
}
