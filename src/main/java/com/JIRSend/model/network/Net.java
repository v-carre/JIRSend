package com.JIRSend.model.network;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.JIRSend.controller.MainController;
import com.JIRSend.model.Message;
import com.JIRSend.model.user.UserEntry;
import com.JIRSend.view.cli.Log;

/*
*  Protocols:
*      - Connection protocol:
*          Broadcast "GetUser"
*          Response: "GetUserResponse $username$" -> store usernames and IPs
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

    public Net(MainController controller, VoidCallback onSetup) {
        this.setupLatch = new CountDownLatch(1);
        this.ipToUserEntry = new HashMap<>();
        this.controller = controller;
        MainController.lostContact.subscribe((ip) -> {
            lostContact(ip);
        });
        MainController.sendMessage.subscribe((message) -> {
            send(getIpFromUsername(message.receiver), "SendMessage " + message.message.replaceAll("\\n", "\\\\n"));
        });
        this.netIO = new NetworkIO(new NetworkCallback(), () -> {
            // signal that setup is complete
            setupLatch.countDown();
        });
        // wait for TCP Server to be started
        try {
            setupLatch.await();
        } catch (InterruptedException e) {
            // re-set the interrupt flag
            Log.e("Net setup was interrupted");
            Thread.currentThread().interrupt();
        }
        broadcast("GetUser");
        onSetup.execute();
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
                        ipToUserEntry.put(senderIP, new UserEntry(true, args));
                        MainController.contactsChange.safePut(args + " is now connected");
                    }
                    break;
                case "NewUser":
                    if (!isUsernameValid(args).equals(okString))
                        Log.l("Forbidden username: " + args, Log.WARNING);
                    else {
                        ipToUserEntry.put(senderIP, new UserEntry(true, args));
                        MainController.contactsChange.safePut(args + " is now connected");
                    }
                    break;
                case "SetOfflineUser":
                    if (ipToUserEntry.containsKey(senderIP)) {
                        ipToUserEntry.get(senderIP).online = false;
                    } else {
                        if (isUsernameValid(args).equals(okString)) {
                            ipToUserEntry.put(senderIP, new UserEntry(false, args));
                            MainController.contactsChange.safePut(args + " has disconnected");
                        } else
                            Log.l("Forbidden username: " + args);
                    }
                    break;
                case "UpdateUsername":
                    if (!isUsernameValid(args).equals(okString))
                        Log.l("Forbidden username: " + args, Log.WARNING);
                    else if (ipToUserEntry.containsKey(senderIP))
                        ipToUserEntry.get(senderIP).username = args;
                    else {
                        ipToUserEntry.put(senderIP, new UserEntry(true, args));
                        MainController.contactsChange.safePut(args + " has updated his username");
                    }
                    break;
                case "SendMessage":
                    if (ipToUserEntry.containsKey(senderIP)) {
                        final String senderUsername = ipToUserEntry.get(senderIP).username;
                        if(ipToUserEntry.get(senderIP).online == false) {
                            ipToUserEntry.get(senderIP).online = true;
                            MainController.contactsChange.safePut(senderUsername + " is now connected");
                        }
                        MainController.messageReceived.safePut(
                                new Message(senderUsername, controller.getUsername(), args.replaceAll("\\\\n", "\n")));
                    } else {
                        //TODO recover "lost" message
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
            // ipToUserEntry.replace(ip, new UserEntry(false,
            // ipToUserEntry.get(ip).username));
            ipToUserEntry.get(ip).online = false;

            MainController.contactsChange.safePut(ipToUserEntry.get(ip).username + " has disconnected");
        }
    }

    /**
     * Get IP associated to a username
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
        Log.l("Broadcasting: Going offline", Log.LOG);
        netIO.broadcast("SetOfflineUser " + controller.getUsername());
    }
}
