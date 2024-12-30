package com.JIRSendAPI;

import com.JIRSendAPI.JIRSendMod.JIRSendModInformation;

public class ModMessage {
    public final JIRSendModInformation mod;
    public final String senderID;
    public final String senderUsername;
    public final String receiverID;
    public final String message;
    public final boolean senderUsernameUpdatable, incommingMessage;

    public ModMessage(JIRSendModInformation info, String senderID, String senderUsername, String receiverID, String message, boolean incommingMessage, boolean senderUsernameUpdatable) {
        this.mod = info;
        this.senderID = senderID;
        this.senderUsername = senderUsername;
        this.receiverID = receiverID;
        this.message = message;
        this.senderUsernameUpdatable = senderUsernameUpdatable;
        this.incommingMessage = incommingMessage;
    }

    public ModMessage(JIRSendModInformation info, String senderID, String senderUsername, String receiverID, String message, boolean incommingMessage) {
        this(info, senderID, senderUsername, receiverID, message, incommingMessage, true);
    }
}
