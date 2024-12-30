package com.JIRSendAPI;

import com.JIRSendAPI.JIRSendMod.JIRSendModInformation;

public class ModMessage {
    public final JIRSendModInformation mod;
    public final String senderID;
    public final String senderUsername;
    public final String receiverID;
    public final String message;
    public final String time;
    public final boolean senderUsernameUpdatable, incommingMessage;

    /**
     * @param info
     * @param senderID
     * @param senderUsername
     * @param receiverID
     * @param message
     * @param time yyyy-MM-dd HH:mm:ss (or at least: should not be superior to 25 chars)
     * @param incommingMessage
     * @param senderUsernameUpdatable
     */
    public ModMessage(JIRSendModInformation info, String senderID, String senderUsername, String receiverID, String message, String time, boolean incommingMessage, boolean senderUsernameUpdatable) {
        this.mod = info;
        this.senderID = senderID;
        this.senderUsername = senderUsername;
        this.receiverID = receiverID;
        this.message = message;
        this.senderUsernameUpdatable = senderUsernameUpdatable;
        this.incommingMessage = incommingMessage;
        this.time = time;
    }

    /**
     * @param info
     * @param senderID
     * @param senderUsername
     * @param receiverID
     * @param message
     * @param time yyyy-MM-dd HH:mm:ss (or at least: should not be superior to 25 chars)
     * @param incommingMessage
     */
    public ModMessage(JIRSendModInformation info, String senderID, String senderUsername, String receiverID, String message, String time, boolean incommingMessage) {
        this(info, senderID, senderUsername, receiverID, message, time, incommingMessage, true);
    }
}
