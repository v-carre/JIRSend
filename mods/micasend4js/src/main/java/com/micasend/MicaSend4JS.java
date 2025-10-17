package com.micasend;

import java.util.ArrayList;

import javax.swing.ImageIcon;

import org.apache.commons.text.StringEscapeUtils;

import com.JIRSendAPI.JIRSendMod;
import com.JIRSendAPI.ModController;
import com.JIRSendAPI.ModMessage;
import com.JIRSendAPI.ModUser;
import com.JIRSendAPI.ModUser.Status;

public class MicaSend4JS implements JIRSendMod {
    private final static String MICASEND_URL = "https://micasend.magictintin.fr";
    private static final String WS_URL = "wss://msws.magictintin.fr:8443";
    private ModController controller;
    private final static String INSTANCE_USERID = "instance";
    private WebSocketClient ws;

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
        this.ws = new WebSocketClient(WS_URL, () -> fetchMessages());
        // ws.sendMessage("Hello world!");
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
        // System.out.println("Sending message to " + recipientID + ": " + message);
        Connector.sendMessage(MICASEND_URL, controller.mainController.getUsername(), message, () -> {
            ws.sendMessage("micasend:new micasend message");
            fetchMessages();
        });
        // fetchMessages();
    }

    @Override
    public void connected() {
        ModController.contactChange.put(new ModUser(MOD_INFO, INSTANCE_USERID, "MicaSend", Status.Online, false));
        fetchMessages();
    }

    private synchronized void fetchMessages() {
        ArrayList<Message> list = Connector.fetchMessages(MICASEND_URL);
        for (Message msg : list) {
            if (msg.id() <= Database.getLast())
                continue;
            ModController.storeMessage
                    .put(new ModMessage(MOD_INFO, INSTANCE_USERID, msg.sender.substring(0, Math.min(msg.sender.length(), 19)), null, StringEscapeUtils.unescapeHtml4(msg.content.replace("§", " ")), msg.date_time, true, false));
            Database.saveLast(msg.id());
            // System.out.println(" MICASEND (" + i + ") >>> [" + msg.sender + "]: " + msg.content.replace("§", " "));
        }
    }

    @FunctionalInterface
    public static interface VoidCallback {
        public void execute();
    }

    public static void main(String[] args) {
        MicaSend4JS mod = new MicaSend4JS();
        mod.initialize(null);
    }
}
