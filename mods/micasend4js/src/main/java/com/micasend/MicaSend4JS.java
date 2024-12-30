package com.micasend;

import java.util.ArrayList;

import javax.swing.ImageIcon;

import com.JIRSendAPI.*;
import com.JIRSendAPI.ModUser.Status;

public class MicaSend4JS implements JIRSendMod {
    private final static String micasendJsonURL = "https://micasend.magictintin.fr/msg?getmsg=json";
    private ModController controller;
    private final static String INSTANCE_USERID = "instance";

    private final JIRSendModInformation MOD_INFO = new JIRSendModInformation(
            "micasend4js",
            "MicaSend for JIRSend",
            "A mod to talk on MicaSend chat from JIRSend",
            "MagicTINTIN", // author
            1, // interface version
            0, // mod version
            new ImageIcon(getClass().getResource("/assets/micasend.png")));

    @Override
    public void initialize(ModController controller) {
        this.controller = controller;
        System.out.println("MicaSend4JS initialized. Welcome!");

    }

    @Override
    public void stop() {
        System.out.println("MicaSend4JS is stopping. Goodbye!");
    }

    @Override
    public JIRSendModInformation getModInformation() {
        return MOD_INFO;
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !username.equals("micasend") && username.length() <= 20 && username.matches("^[a-zA-Z0-9_-]+$");
    }

    @Override
    public void changeUsername(String username) {
    }

    @Override
    public void sendMessage(String recipientID, String message) {
        System.out.println("Sending message to " + recipientID + ": " + message);
    }

    @Override
    public void connected() {
        ModController.contactChange.put(new ModUser(MOD_INFO, INSTANCE_USERID, "MicaSend", Status.Online, false));
        ArrayList<Message> list = Connector.fetchMessages(micasendJsonURL);
        for (Message msg : list) {
            if (msg.id() <= Database.getLast())
                continue;
            ModController.storeMessage
                    .put(new ModMessage(MOD_INFO, INSTANCE_USERID, msg.sender.substring(0, 19), null, msg.content.replace("§", " "), true, false));
            Database.saveLast(msg.id());
            // System.out.println(" MICASEND (" + i + ") >>> [" + msg.sender + "]: " + msg.content.replace("§", " "));
        }
    }
}
