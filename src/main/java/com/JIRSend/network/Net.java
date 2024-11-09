package com.JIRSend.network;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import com.JIRSend.controller.MainController;
import com.JIRSend.ui.Log;
import com.JIRSend.ui.MainWindow;

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

    public Net(MainController controller) {
        this.controller = controller;
        this.netIO = new NetworkIO(new NetworkCallback());
        broadcast("GetUser");
        ipToUserEntry = new HashMap<>();
    }

    public boolean usernameAvailable(String username) {
        if (!isUsernameValid(username))
            return false;
        for (UserEntry entry : ipToUserEntry.values())
            if (entry.username.equals(username))
                return false;
        broadcast("NewUser " + username);
        printHashMap();
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
            Log.l("received: \""+command+"\" \""+args+"\"",Log.LOG);
            switch (command) {
                case "GetUser":
                    String username = window.getUsername();
                    if(username != null)
                        send(senderIP, "GetUserResponse " + username);
                    break;
                case "GetUserResponse":
                    if (!isUsernameValid(args))
                        Log.l("Forbidden username: " + args, Log.WARNING);
                    else
                        ipToUserEntry.put(senderIP, new UserEntry(true, args));
                    break;
                case "NewUser":
                    if (!isUsernameValid(args))
                        Log.l("Forbidden username: " + args, Log.WARNING);
                    else
                        ipToUserEntry.put(senderIP, new UserEntry(true, args));
                    break;
                case "SetOfflineUser":
                    if (ipToUserEntry.containsKey(senderIP)) {
                        ipToUserEntry.get(senderIP).online = false;
                    } else {
                        if (isUsernameValid(args))
                            ipToUserEntry.put(senderIP, new UserEntry(false, args));
                        else
                            Log.l("Forbidden username: " + args);
                    }
                    break;
                case "UpdateUsername":
                    if (!isUsernameValid(args))
                        Log.l("Forbidden username: " + args, Log.WARNING);
                    else if (ipToUserEntry.containsKey(senderIP))
                        ipToUserEntry.get(senderIP).username = args;
                    else
                        ipToUserEntry.put(senderIP, new UserEntry(true, args));
                    break;
                case "SendMessage":
                    if (ipToUserEntry.containsKey(senderIP)) // Maybe set user to online = true
                        System.out.println("[" + ipToUserEntry.get(senderIP).username + "] " + args);
                    else {
                        // FIXME Message was lost
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

    public class UserEntry {
        public boolean online;
        public String username;

        public UserEntry(boolean online, String username) {
            this.online = online;
            this.username = username;
        }

        @Override
        public String toString() {
            return username + (online ? "(online)" : "(offline)");
        }
    }

    public boolean isUsernameValid(String username) {
        return !username.contains(":");
    }

    private boolean send(String address, String string) {
        Log.l("Sending: " + string, Log.LOG);
        return netIO.send(address, string);
    }

    private void broadcast(String string) {
        Log.l("Broadcasting: " + string, Log.LOG);
        netIO.broadcast(string);
    }
}
