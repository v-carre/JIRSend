package com.JIRSend.network;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

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
    private final MainWindow window;

    public Net(MainWindow mainWindwow) {
        window = mainWindwow;
        netIO = new NetworkIO(new NetworkCallback());
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
        public void execute(InetAddress senderAddress, int senderPort, String value, boolean isBroadcast) {
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
            switch (command) {
                case "GetUser":
                    String username = window.getUsername();
                    username = username == null ? "placeholderUsername" : username;
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

    public HashMap<String,UserEntry> getHashMap() {
        return ipToUserEntry;
    }

    public void printHashMap() {
        if(ipToUserEntry.isEmpty()) {
            System.out.println("{}");
            return;
        }
        System.out.println("{");
        for(Map.Entry<String,UserEntry> e : ipToUserEntry.entrySet()) {
            System.out.println("\t"+e.getKey()+":"+e.getValue() );
        }
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

    private void send(String address, String string) {
        Log.l("Sending: "+string,Log.LOG);
        netIO.send(address, string);
    }

    private void broadcast(String string) {
        Log.l("Broadcasting: "+string,Log.LOG);
        netIO.broadcast(string);
    }

    /*
     * private ClientServerSocket mainServer;
     * private DatagramSocket broadcastSocket = null;
     * static public final int broadcastPort = 11572;
     * 
     * public Net() {
     * mainServer = new ClientServerSocket();
     * mainServer.start();
     * }
     * 
     * public boolean usernameAvailable(String username) {
     * return !username.isEmpty();
     * }
     * 
     * private void broadcast(String msg) {
     * if(broadcastSocket==null) {
     * try {
     * broadcastSocket = new
     * DatagramSocket(broadcastPort,InetAddress.getLocalHost());
     * } catch (SocketException e) {
     * e.printStackTrace();
     * } catch (UnknownHostException e) {
     * e.printStackTrace();
     * }
     * try {
     * broadcastSocket.setBroadcast(true);
     * } catch (SocketException e) {
     * e.printStackTrace();
     * }
     * }
     * 
     * byte[] buff = new byte[256];
     * buff = msg == null ? "plz connect uwu".getBytes() : msg.getBytes();
     * 
     * InetAddress address = null;
     * try {
     * address = InetAddress.getByName("255.255.255.255");
     * } catch (UnknownHostException e) {
     * e.printStackTrace();
     * }
     * 
     * DatagramPacket request;
     * if(address!=null)
     * request = new DatagramPacket(buff, buff.length, address ,broadcastPort);
     * else
     * return;
     * try {
     * broadcastSocket.send(request);
     * } catch (IOException e) {
     * e.printStackTrace();
     * }
     * broadcastSocket.close();
     * //TODO: SET RECEIVING SOCKET FOR CONNECTION !
     * }
     */
}
