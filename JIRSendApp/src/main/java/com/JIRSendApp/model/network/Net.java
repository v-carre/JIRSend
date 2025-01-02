package com.JIRSendApp.model.network;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.JIRSendApp.controller.MainController;
import com.JIRSendApp.model.Message;
import com.JIRSendApp.model.db.LocalDatabase.DatabaseMessage;
import com.JIRSendApp.model.db.LocalDatabase.IDandUsername;
import com.JIRSendApp.model.user.UserEntry;
import com.JIRSendApp.model.user.UserEntry.Status;
import com.JIRSendApp.view.cli.Log;

/*
*  Protocols:
*      - Connection protocol:
*          Broadcast "GetUser"
*          Response: "GetUserResponse $" -> store usernames and IPs
*      - GetUser
*      - GetUserResponse username
*      - NewUser username
*      - SetOfflineUser username
*      - UpdateUsername new
*      - SendMessage msg
 */
public class Net {
    private NetworkIO netIO;
    private HashMap<String, UserEntry> ipToUserEntry;
    private final MainController controller;
    private final CountDownLatch setupLatch;
    public static final String okString = "";
    private boolean online = false;

    public Net(MainController controller, VoidCallback onSetup, boolean test) throws SocketException {
        this.online = false;
        this.setupLatch = new CountDownLatch(1);
        this.ipToUserEntry = new HashMap<>();
        this.controller = controller;
        addDBContacts();
        MainController.lostContact.subscribe((ip) -> {
            lostContact(ip);
        });
        MainController.sendMessage.subscribe((message) -> {
            if (message.modMessage)
                return;
            final String addrDest = getIpFromUsername(message.receiver);

            // reroute to mod if it is a user id of a mod
            if (addrDest.startsWith("-")) {
                controller.sendMessageToMod(addrDest, message.message);
                return;
            }

            send(addrDest, "SendMessage " + message.message.replaceAll("\\n", "\\\\n"));
            MainController.databaseMessage
                    .safePut(new DatabaseMessage(addrDest, message.sender, message.message, message.time, true, true));
        });
        if (test)
            this.netIO = new NetworkIO(new NetworkCallback(), () -> {
                // signal that setup is complete
                setupLatch.countDown();
            }, true);
        else
            this.netIO = new NetworkIO(new NetworkCallback(), () -> {
                // signal that setup is complete
                setupLatch.countDown();
            });
        // wait for TCP Server to be started
        try {
            setupLatch.await();
            broadcast("GetUser");
            onSetup.execute();
            online = true;
        } catch (InterruptedException e) {
            // re-set the interrupt flag
            Log.e("Net setup was interrupted");
            Thread.currentThread().interrupt();
        }
    }

    public Net(MainController controller, VoidCallback onSetup) throws SocketException {
        this(controller, onSetup, false);
    }

    private void addDBContacts() {
        if (controller == null)
            return;
        ArrayList<IDandUsername> dbc = controller.getDBContacts();
        for (IDandUsername c : dbc) {
            this.ipToUserEntry.put(c.id, new UserEntry(Status.Offline, c.username));
        }
    }

    public void stop() {
        this.online = false;
        this.netIO.stop();
    }

    public void contactsChangePut(String value) {
        if (online)
            MainController.contactsChange.safePut(value);
    }

    private void contactDBUpdate(String ip, String username) {
        MainController.databaseContact.safePut(new IDandUsername(ip, username, true));
    }

    /**
     * Takes the username if available
     * 
     * @param username
     * @return error | Net.okString if available
     */
    public String usernameAvailable(String username) {
        String isSyntaxValid = isUsernameValid(username);
        if (!isSyntaxValid.equals(okString))
            return isSyntaxValid;
        for (UserEntry entry : ipToUserEntry.values())
            if (entry.username.equals(username))
                return "'" + username + "' is not available!";
        broadcast("NewUser " + username);
        return okString;
    }

    /**
     * Takes the username if available for a mod
     * 
     * @param modID    will
     * @param username
     * @return if available
     */
    public boolean usernameAvailable(String username, String modID) {
        String isSyntaxValid = isUsernameValid(username);
        if (!isSyntaxValid.equals(okString))
            return false;
        for (String id : ipToUserEntry.keySet()) {
            UserEntry ue = ipToUserEntry.get(id);
            if (ue.username.equals(username) && !id.startsWith("-" + modID + " "))
                return false;
        }
        // broadcast("NewUser " + username);
        return true;
    }

    private class NetworkCallback extends NetCallback {
        @Override
        public void execute(InetAddress senderAddress, int senderPort, String value, boolean isBroadcast,
                boolean isUDP) {
            final String senderIP = senderAddress.getHostAddress();
            Log.l("[" + senderIP + ":" + senderPort + "] " + value, Log.LOG);
            final String command;
            final String args;
            if (value.equals("GetUser")) {
                command = value;
                args = null;
            } else {
                final String[] splited = value.split(" ", 2);
                if (splited.length != 2) {
                    Log.e("Wrong message format: " + value);
                    return;
                }
                command = splited[0];
                args = splited[1];
            }
            Log.l("received: \"" + command + "\" \"" + args + "\"", Log.LOG);
            switch (command) {
                case "GetUser":
                    String username = controller.getUsername();
                    if (username != null)
                        send(senderIP, "GetUserResponse " + username);
                    break;
                case "GetUserResponse":
                    if (!isUsernameValid(args).equals(okString))
                        Log.l("Forbidden username: " + args, Log.WARNING);
                    else {
                        ipToUserEntry.put(senderIP, new UserEntry(Status.Online, args));
                        contactDBUpdate(senderIP, args);
                        contactsChangePut(args + " is now connected");
                    }
                    break;
                case "NewUser":
                    if (!isUsernameValid(args).equals(okString))
                        Log.l("Forbidden username: " + args, Log.WARNING);
                    else {
                        if (controller.getUsername().equals(args))
                            break;
                        else if (ipToUserEntry.containsKey(senderIP)
                                && !ipToUserEntry.get(senderIP).username.equals(args)) {
                            final UserEntry oldUE = new UserEntry(ipToUserEntry.get(senderIP).online,
                                    ipToUserEntry.get(senderIP).username);
                            ipToUserEntry.get(senderIP).username = args;
                            ipToUserEntry.get(senderIP).online = Status.Online;
                            contactDBUpdate(senderIP, args);
                            contactsChangePut(oldUE.username + " changed his username to "
                                    + args + (oldUE.online() ? "" : " and is now connected"));
                        } else {
                            ipToUserEntry.put(senderIP, new UserEntry(Status.Online, args));
                            contactDBUpdate(senderIP, args);
                            contactsChangePut(args + " is now connected");
                        }
                    }
                    break;
                case "SetOfflineUser":
                    if (ipToUserEntry.containsKey(senderIP)) {
                        boolean change = false;
                        if (ipToUserEntry.get(senderIP).online())
                            change = true;
                        ipToUserEntry.get(senderIP).online = Status.Offline;
                        if (change)
                            contactsChangePut(args + " has disconnected");
                    } else {
                        if (isUsernameValid(args).equals(okString)) {
                            ipToUserEntry.put(senderIP, new UserEntry(Status.Offline, args));
                            contactDBUpdate(senderIP, args);
                            contactsChangePut(args + " has disconnected");
                        } else
                            Log.l("Forbidden username: " + args);
                    }
                    break;
                case "UpdateUsername":
                    if (!isUsernameValid(args).equals(okString))
                        Log.l("Forbidden username: " + args, Log.WARNING);
                    else if (ipToUserEntry.containsKey(senderIP)) {
                        MainController.contactsChange
                                .safePut(ipToUserEntry.get(senderIP) + " changed his username to" + args);
                        ipToUserEntry.get(senderIP).username = args;
                        contactDBUpdate(senderIP, args);
                    } else {
                        ipToUserEntry.put(senderIP, new UserEntry(Status.Online, args));
                        contactDBUpdate(senderIP, args);
                        contactsChangePut(args + " is now connected");
                    }
                    break;
                case "SendMessage":
                    if (ipToUserEntry.containsKey(senderIP)) {
                        final String senderUsername = ipToUserEntry.get(senderIP).username;
                        if (ipToUserEntry.get(senderIP).online() == false) {
                            ipToUserEntry.get(senderIP).online = Status.Online;
                            contactsChangePut(senderUsername + " is now connected");
                        }
                        final String messageContent = args.replaceAll("\\\\n", "\n");
                        final String time = controller.getTime();
                        MainController.databaseMessage
                                .safePut(new DatabaseMessage(senderIP, senderUsername, messageContent, time, false,
                                        false));
                        MainController.messageReceived
                                .safePut(new Message(senderUsername, controller.getUsername(), messageContent, time));
                    } else {
                        // TODO recover "lost" message
                        send(senderIP, "GetUser");
                        System.out.println("[Unkown user] " + args);
                    }
                    break;
                default:
                    Log.l("Unkown command: " + command + " " + value, Log.LOG);
                    break;
            }
        }

    }

    public HashMap<String, UserEntry> getHashMap() {
        return ipToUserEntry;
    }

    public void printHashMap() {
        if (ipToUserEntry.isEmpty()) {
            System.out.println("{}");
            return;
        }
        System.out.println("{");
        for (Map.Entry<String, UserEntry> e : ipToUserEntry.entrySet()) {
            System.out.println("\t" + e.getKey() + ":" + e.getValue());
        }
        System.out.println("}");
    }

    public ArrayList<UserEntry> getUserEntries() {
        ArrayList<UserEntry> l = new ArrayList<>();
        for (UserEntry ue : ipToUserEntry.values()) {
            l.add(ue);
        }
        return l;
    }

    public UserEntry getUserEntryIfExist(String ip) {
        if (ipToUserEntry.containsKey(ip))
            return ipToUserEntry.get(ip);
        else
            return null;
    }

    /**
     * Returns whether a username syntax is valid
     * 
     * @param username
     * @return error | Net.okString if valid
     */
    private String isUsernameValid(String username) {
        if (username.contains(":"))
            return "Username should not contain ':'!";
        else if (username.contains(" "))
            return "Username should not contain spaces ' '!";
        else if (username.length() < 2)
            return "Username should have at least 2 characters!";
        else if (username.length() > 17)
            return "Username should have at most 17 characters!";
        return okString;
    }

    private boolean send(String address, String string) {
        Log.l("Sending: " + string, Log.LOG);
        return netIO.send(address, string);
    }

    private void broadcast(String string) {
        Log.l("Broadcasting: " + string, Log.LOG);
        netIO.broadcast(string);
    }

    private void lostContact(String ip) {
        if (ipToUserEntry.containsKey(ip)) {
            boolean change = false;
            if (ipToUserEntry.get(ip).online())
                change = true;
            ipToUserEntry.get(ip).online = Status.Offline;
            if (change)
                contactsChangePut(ipToUserEntry.get(ip).username + " has disconnected");
        }
    }

    public void updateContacts(String id, UserEntry ue) {
        ipToUserEntry.put(id, ue);
    }

    /**
     * Get IP associated to a username
     * 
     * @param username
     * @return ip string | null if not found
     */
    public String getIpFromUsername(String username) {
        for (Map.Entry<String, UserEntry> pair : ipToUserEntry.entrySet()) {
            if (pair.getValue().username.equals(username)) {
                return pair.getKey();
            }
        }
        return null;
    }

    public void sendGoingOfflineMessage() {
        if (controller.getUsername() == null)
            return;
        Log.l("Broadcasting: Going offline", Log.LOG);
        netIO.broadcast("SetOfflineUser " + controller.getUsername());
    }
}
