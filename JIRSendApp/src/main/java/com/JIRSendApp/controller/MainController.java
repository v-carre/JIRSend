package com.JIRSendApp.controller;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.JIRSendAPI.ModController;
import com.JIRSendApp.model.Message;
import com.JIRSendApp.model.db.LocalDatabase;
import com.JIRSendApp.model.db.LocalDatabase.DatabaseMessage;
import com.JIRSendApp.model.db.LocalDatabase.IDandUsername;
import com.JIRSendApp.model.network.Net;
import com.JIRSendApp.model.user.BaseUser;
import com.JIRSendApp.model.user.Conversation;
import com.JIRSendApp.model.user.User;
import com.JIRSendApp.model.user.UserEntry;
import com.JIRSendApp.view.MainAbstractView;
import com.JIRSendApp.view.cli.CliTools;
import com.JIRSendApp.view.cli.MainCLI;
import com.JIRSendApp.view.gui.ErrorPopup;
import com.JIRSendApp.view.gui.MainGUI;

public class MainController {
    private String controllerName;
    public final ModController modc;
    private final APIControllerActions apiActions;

    // View objects
    private MainAbstractView view;
    private boolean usingGUI;

    // Model objects
    protected BaseUser user;
    protected Net net;
    protected LocalDatabase db;

    // Pipes
    public static Pipe<String> lostContact = new Pipe<>("Lost Contact");
    public static Pipe<String> localUsernameChange = new Pipe<>("Local Username Changed");
    public static Pipe<String> contactsChange = new Pipe<>("Contacts Changed");
    public static Pipe<IDandUsername> databaseContact = new Pipe<>("Contact update in DB");
    public static Pipe<Message> sendMessage = new Pipe<>("Sending Message");
    public static Pipe<Message> messageReceived = new Pipe<>("Message received");
    public static Pipe<DatabaseMessage> databaseMessage = new Pipe<>("Message update in DB");

    public MainController(String name, boolean usingGUI) throws SocketException {
        this.controllerName = name;
        this.usingGUI = usingGUI;
        if (usingGUI)
            if (!GraphicsEnvironment.isHeadless())
                this.view = new MainGUI(this);
            else {
                CliTools.printBigError("No X11 display found. Starting Command Line Interface instead...");
                this.view = new MainCLI(this);
                this.usingGUI = false;
            }
        else
            this.view = new MainCLI(this);
        this.db = new LocalDatabase();
        // connect db
        if (!this.db.connect()) {
            CliTools.printBigError(
                    "Could not load/create local save: Unable to create local database. Check your permissions!");
            if (this.usingGUI)
                ErrorPopup.show("Could not load/create local save",
                        "Unable to create local database. Check your permissions!");
            System.exit(4);
        }
        this.user = new User(this);

        this.apiActions = new APIControllerActions(this);
        this.modc = new ModController(apiActions);
        // start UI when Net is setup
        this.net = new Net(this, () -> {
            startUI();
        });
    }

    public MainController(boolean usingGUI) throws SocketException {
        this("JIRSend Main", usingGUI);
    }

    public void startUI() {
        try {
            this.view.open();
        } catch (HeadlessException e) {
            System.err.println(
                    "Could not find X11 display. Opening a Command Line Interface instead. Consider using --cli option.");
            this.view = new MainCLI(this);
            this.view.open();
        }
    }

    public String getName() {
        return controllerName;
    }

    public void stopNet() {
        this.net.stop();
    }

    /**
     * Will stop the app
     */
    public void stoppingApp(int exitStatus) {
        net.sendGoingOfflineMessage();
        stopNet();
        if (modc != null)
            modc.stop();
        System.exit(exitStatus);
    }

    /**
     * Will stop the app normally
     */
    public void stoppingApp() {
        stoppingApp(0);
    }

    public String changeUsername(String username) {
        String res = this.net.usernameAvailable(username);
        res += modc.isUsernameAvailable(username) ? "" : " [MOD] Not a valid username.";
        if (res.equals(Net.okString)) {
            this.user.setUsername(username);
            modc.changeUsername(username);
            return Net.okString;
        }
        return res;
    }

    public String getUsername() {
        return this.user.getUsername();
    }

    public ArrayList<UserEntry> getContacts() {
        return net.getUserEntries();
    }

    public int getNumberConnected() {
        int connected = 0;
        for (UserEntry ue : net.getUserEntries()) {
            if (ue.online)
                connected++;
        }
        return connected;
    }

    public boolean isConnected(String name) {
        boolean connected = false;
        for (UserEntry ue : net.getUserEntries()) {
            if (ue.username == name) {
                connected = ue.online;
                break;
            }
        }
        return connected;
    }

    public ArrayList<String> getConnectedUsernames() {
        ArrayList<String> connected = new ArrayList<>();
        for (UserEntry ue : net.getUserEntries()) {
            if (ue.online)
                connected.add(ue.username);
        }
        return connected;
    }

    public String getIPFromUsername(String username) {
        if (net == null)
            return null;
        return net.getIpFromUsername(username);
    }

    public String getUsernameFromIP(String ip) {
        UserEntry ue = net.getHashMap().get(ip);
        if (ue == null)
            return null;
        return ue.username;
    }

    public String getConversationName() {
        return user.getCurrentConversationName();
    }

    public Conversation getConversation() {
        String convIp = getConversationIP();
        if (convIp == null)
            return null;
        return user.getConversation(convIp);
    }

    public String getConversationIP(String convName) {
        if (convName == null)
            return null;
        return net.getIpFromUsername(convName);
    }

    public String getConversationIP() {
        return getConversationIP(user.getCurrentConversationName());
    }

    public int getConversationUnreadNumber(String name) {
        String ip = getConversationIP(name);
        if (ip == null)
            return 0;
        return this.user.getConversationUnreadNb(ip);
    }

    public Conversation getConversation(String name) {
        if (name == null)
            return null;
        String ip = net.getIpFromUsername(name);
        if (ip == null)
            return null;
        Conversation conv = user.getConversation(ip);
        if (conv != null)
            user.setCurrentConversationName(name);

        return conv;
    }

    public int getTotalUnread() {
        return user.getTotalUnread();
    }

    public void markConversationRead(String name) {
        String ip = net.getIpFromUsername(name);
        if (ip == null)
            return;
        user.markConversationRead(ip);
        db.markMessagesRead(ip);
    }

    public ArrayList<IDandUsername> getDBContacts() {
        return db.getDBContacts();
    }

    public ArrayList<DatabaseMessage> getMessagesFromContact(String contactID) {
        return db.getMessagesFromContact(contactID);
    }

    public ArrayList<DatabaseMessage> getAllMessagesFromDB() {
        return db.getAllMessagesFromDB();
    }

    public String getTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    public boolean isUsingGUI() {
        return usingGUI;
    }
}
