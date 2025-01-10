package com.JIRSendApp.controller;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import com.JIRSendAPI.ModController;
import com.JIRSendAPI.ModMessage;
import com.JIRSendAPI.ModUser;
import com.JIRSendApp.model.Message;
import com.JIRSendApp.model.db.LocalDatabase;
import com.JIRSendApp.model.db.LocalDatabase.DatabaseMessage;
import com.JIRSendApp.model.db.LocalDatabase.IDandUsername;
import com.JIRSendApp.model.network.Net;
import com.JIRSendApp.model.user.BaseUser;
import com.JIRSendApp.model.user.Conversation;
import com.JIRSendApp.model.user.User;
import com.JIRSendApp.model.user.UserEntry;
import com.JIRSendApp.model.user.UserEntry.Status;
import com.JIRSendApp.view.MainAbstractView;
import com.JIRSendApp.view.cli.CliTools;
import com.JIRSendApp.view.cli.MainCLI;
import com.JIRSendApp.view.gui.ErrorPopup;
// import com.JIRSendApp.view.gui.LoadingPopup;
import com.JIRSendApp.view.gui.MainGUI;
import com.JIRSendApp.view.sound.SoundPlayer;

public class MainController {
    private String controllerName;
    public final ModController modc;
    private final APIControllerActions apiActions;

    // View objects
    private MainAbstractView view;
    private boolean usingGUI;

    // Model objects
    protected BaseUser user;
    private boolean connnected = false;
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
        this.connnected = false;
        if (usingGUI)
            if (!GraphicsEnvironment.isHeadless()) {
                // LoadingPopup.start();
                this.view = new MainGUI(this);
                startUI();
            }
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
        setupLink();

        this.apiActions = new APIControllerActions(this);
        this.modc = new ModController(apiActions);
        // start UI when Net is setup
        this.net = new Net(this, () -> {
            modc.initializeMods();
            this.view.open();
            // LoadingPopup.stop();
        });
        new SoundPlayer();
    }

    public MainController(boolean usingGUI) throws SocketException {
        this("JIRSend Main", usingGUI);
    }

    public void startUI() {
        try {
            this.view.start();
        } catch (HeadlessException e) {
            this.usingGUI = false;
            System.err.println(
                    "Could not find X11 display. Opening a Command Line Interface instead. Consider using --cli option.");
            this.view = new MainCLI(this);
        }
    }

    public String getName() {
        return controllerName;
    }

    public void stopNet() {
        if (net == null)
            return;
        this.net.stop();
        this.net.sendGoingOfflineMessage();
    }

    /**
     * Will stop the app
     */
    public void stoppingApp(int exitStatus) {
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
            if (!this.connnected) {
                this.connnected = true;
                modc.nowConnected();
            }
            modc.changeUsername(username);
            return Net.okString;
        }
        return res;
    }

    public boolean isUsernameAvailableLocal(String username) {
        String res = this.net.usernameAvailable(username);
        if (res.equals(Net.okString))
            return true;
        return false;
    }

    public boolean isUsernameAvailableMod(String username, String modID) {
        return this.net.usernameAvailable(username, modID);
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
            if (ue.online())
                connected++;
        }
        return connected;
    }

    public boolean isConnected(String name) {
        boolean connected = false;
        for (UserEntry ue : net.getUserEntries()) {
            if (ue.username == name) {
                connected = ue.online();
                break;
            }
        }
        return connected;
    }

    public ArrayList<String> getConnectedUsernames() {
        ArrayList<String> connected = new ArrayList<>();
        for (UserEntry ue : net.getUserEntries()) {
            if (ue.online())
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

    public ImageIcon getConversationIcon() {
        String ip = user.getCurrentConversationIP();
        if (ip == null)
            return null;
        UserEntry ue = net.getUserEntryIfExist(ip);
        if (ue == null)
            return null;
        return ue.icon;
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

    public UserEntry getUserEntryIfExists(String ip) {
        return net.getUserEntryIfExist(ip);
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

    public Message getConversationLastMessage(String name) {
        if (name == null)
            return null;
        String ip = net.getIpFromUsername(name);
        if (ip == null)
            return null;
        Conversation conv = user.getConversation(ip);
        if (conv.getMessages().isEmpty())
            return null;
        return conv.getMessages().get(conv.getMessages().size()-1);
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

    public boolean isDBContactUpdatable(String contactID) {
        return db.isDBContactUpdatable(contactID);
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

    // MODS

    public static class ModIDAndUserID {
        public String modID, userID;

        public ModIDAndUserID(String modID, String userID) {
            this.modID = modID;
            this.userID = userID;
        }
    }

    public static String getContactFromModUser(ModUser user) {
        return "-" + user.mod.id + " " + user.userID;
    }

    public static String getContactFromModMessage(ModMessage msg) {
        return "-" + msg.mod.id + " " + (msg.incommingMessage ? msg.senderID : msg.receiverID);
    }

    public static ModIDAndUserID getContactFromModUser(String idContact) {
        String[] parts = idContact.split("\\ ");
        return new ModIDAndUserID(parts[0].substring(1), parts[1]);
    }

    public void sendMessageToMod(String contactID, String messsage) {
        ModIDAndUserID modUsrID = getContactFromModUser(contactID);
        modc.sendMessageViaMod(modUsrID.modID, modUsrID.userID, messsage);
    }

    public static Status statusConverter(ModUser.Status status) {
        switch (status) {
            case Offline:
                return Status.Offline;
            case Busy:
                return Status.Busy;
            case Away:
                return Status.Away;
            case Online:
                return Status.Online;

            default:
                return Status.Online;
        }
    }

    private void setupLink() {
        ModController.contactChange.subscribe(user -> {

            UserEntry c = net.getUserEntryIfExist(getContactFromModUser(user));
            net.updateContacts(getContactFromModUser(user),
                    new UserEntry(statusConverter(user.online), user.username, user.mod.modIcon));
            db.updateContactInDB(new IDandUsername(getContactFromModUser(user), user.username, user.updateUsername));

            if (c == null || c.username == user.username) {
                if (user.online() && (c == null || !c.online()))
                    contactsChange.safePut("[" + user.mod.name + "] " + user.username + " is now connected");
                else if (!user.online() && (c == null || c.online()))
                    contactsChange.safePut("[" + user.mod.name + "] " + user.username + " has disconnected");
            } else {
                if (user.online() && !c.online())
                    contactsChange.safePut("[" + user.mod.name + "] " + c.username + " changed his username to "
                            + user.username + " and is now connected");
                else if (!user.online() && c.online())
                    contactsChange.safePut("[" + user.mod.name + "] " + c.username + " changed his username to "
                            + user.username + " and has disconnected");
            }
        });

        ModController.storeMessage.subscribe(message -> {
            String senderUsername = message.senderUsernameUpdatable
                    ? (message.incommingMessage ? BaseUser.senderString : BaseUser.youString)
                    : (message.incommingMessage ? message.senderUsername : getUsername());
            String receiverUsername = message.senderUsernameUpdatable
                    ? (message.incommingMessage ? BaseUser.senderString : BaseUser.youString)
                    : (message.incommingMessage ? message.senderUsername : getUsername());

            user.addToConversation(getContactFromModMessage(message),
                    new Message(senderUsername, receiverUsername, message.message, message.time));
                    
            MainController.databaseMessage
                    .safePut(new DatabaseMessage(getContactFromModMessage(message),
                            message.senderUsername, message.message, message.time, !message.incommingMessage, false));
            if (message.incommingMessage)
                MainController.messageReceived
                        .safePut(new Message(message.senderUsername, getUsername(), message.message, message.time, true));
            else
                MainController.sendMessage
                        .safePut(new Message(getUsername(), getConversationName(), message.message, message.time, true));
        });
    }
}
