package com.JIRSendAPI;

import com.JIRSendAPI.JIRSendMod.JIRSendModInformation;

public class ModMessage {
    public final JIRSendModInformation mod;
    public final String senderID;
    public final String senderUsername;
    public final String receiverID;
    public final String message;

    public ModMessage(JIRSendModInformation info, String senderID, String senderUsername, String receiverID, String message) {
        this.mod = info;
        this.senderID = senderID;
        this.senderUsername = senderUsername;
        this.receiverID = receiverID;
        this.message = message;
    }
}
