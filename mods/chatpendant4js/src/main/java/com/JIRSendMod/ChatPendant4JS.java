package com.JIRSendMod;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.ImageIcon;
import com.JIRSendAPI.*;
import com.JIRSendAPI.ModUser.Status;

public class ChatPendant4JS implements JIRSendMod {
    private ModController controller;

    private String getTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    private final JIRSendModInformation MOD_INFO = new JIRSendModInformation(
            "chatpendant4js",
            "ChatPendant for JIRSend",
            "A mod to talk with ChatPendant from JIRSend",
            "MagicTINTIN", // author
            1, // interface version
            0, // mod version
            new ImageIcon(getClass().getResource("/assets/chatpendant.png")));

    @Override
    public void initialize(ModController controller) {
        this.controller = controller;
    }

    @Override
    public void stop() {
    }

    @Override
    public JIRSendModInformation getModInformation() {
        return MOD_INFO;
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !username.equals("Notes") && !username.equals("Note") && !username.equals("notes")
                && !username.equals("note") && username.length() <= 20 && username.matches("^[a-zA-Z0-9_-]+$");
    }

    @Override
    public void changeUsername(String username) {
    }

    @Override
    public void sendMessage(String recipientID, String message) {
        ModController.storeMessage.put(new ModMessage(MOD_INFO, "local", controller.mainController.getUsername(),
                "notes", message, getTime(), false));
    }

    @Override
    public void connected() {
        ModController.contactChange.put(new ModUser(MOD_INFO, "notes", "Notes", Status.Online));
    }
}
