package com.JIRSend.mods;

import com.JIRSend.mods.JIRSendMod.JIRSendModInformation;

public class ModMessage {
    public final JIRSendModInformation mod;
    public final String senderID;
    public final String receiverID;
    public final String message;

    public ModMessage(JIRSendModInformation info, String senderID, String receiverID, String message) {
        this.mod = info;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
    }
}
