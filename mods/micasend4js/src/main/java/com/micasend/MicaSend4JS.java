package com.micasend;

import javax.swing.ImageIcon;
import com.JIRSendAPI.*;

public class MicaSend4JS implements JIRSendMod {

    private ModController controller;
    private String username;
    private final JIRSendModInformation MOD_INFO = new JIRSendModInformation(
        "micasend4js",
        "MicaSend for JIRSend",
        "A mod to talk on MicaSend chat from JIRSend",
        "MagicTINTIN", // author
        1, // interface version
        0, // mod version
        new ImageIcon("assets/micasend.png")
    );

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
        this.username = username;
        System.out.println("Username changed to: " + username);
    }

    @Override
    public void sendMessage(String recipientID, String message) {
        System.out.println("Sending message to " + recipientID + ": " + message);
    }
}
