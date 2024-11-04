package com.JIRSend.network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;

import com.JIRSend.ui.Log;

/*
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
*/


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
    private HashMap<String,UserEntry> ipToUserEntry;


    public Net() {
        netIO = new NetworkIO(new NetworkCallback());
        netIO.broadcast("GetUser");
        ipToUserEntry = new HashMap<>();
    }

    public boolean usernameAvailable(String username) {
        if (!isUsernameValid(username))
            return false;
        for( UserEntry entry : ipToUserEntry.values())
            if(entry.username.equals(username))
                return false;
        netIO.broadcast("NewUser "+username);
        return true;
    }

    private class NetworkCallback extends NetCallback {
        @Override
        public void execute(InetAddress senderAddress, int senderPort, String value, boolean isBroadcast) {
            final String senderIP = senderAddress.getHostAddress();
            Log.l("[" + senderIP + ":" + senderPort + "] " + value,Log.LOG);
            final String command;
            final String args;
            if(value.equals("GetUser"))
            {
                command = value;
                args = null;
            }
            else
            {
                final String[] splited = value.split(" ",2);
                if (splited.length != 2 ) {
                    Log.e("Wrong message format: "+value);
                    return;
                }
                command = splited[0];
                args = splited[1];
            }
            switch (command) {
                case "GetUser":
                    netIO.send(senderIP, "GetUserResponse placeholderMyUsername");
                    break;
                case "GetUserResponse":
                    if (!isUsernameValid(args))
                        Log.l("Forbidden username: "+args,Log.WARNING);
                    else
                        ipToUserEntry.put(senderIP,new UserEntry(true, args));
                    break;
                case "NewUser":
                    if(!isUsernameValid(args)) 
                        Log.l("Forbidden username: "+args,Log.WARNING);
                    else
                        ipToUserEntry.put(senderIP,new UserEntry(true, args));
                    break;
                case "SetOfflineUser":
                    if (ipToUserEntry.containsKey(senderIP)) {
                        ipToUserEntry.get(senderIP).online = false;
                    } else {
                        if (isUsernameValid(args))
                            ipToUserEntry.put(senderIP,new UserEntry(false, args));
                        else
                            Log.l("Forbidden username: " + args);
                    }
                    break;
                case "UpdateUsername":
                    if(!isUsernameValid(args))
                        Log.l("Forbidden username: "+args,Log.WARNING);
                    else
                        if(ipToUserEntry.containsKey(senderIP))
                            ipToUserEntry.get(senderIP).username = args;
                        else
                            ipToUserEntry.put(senderIP,new UserEntry(true, args));
                    break;
                case "SendMessage":
                    if(ipToUserEntry.containsKey(senderIP)) //Maybe set user to online = true
                        System.out.println("["+ipToUserEntry.get(senderIP).username + "] " + args);
                    else {
                        // FIXME Message was lost
                        netIO.send(senderIP, "GetUser");
                        System.out.println("[Unkown user] "+args); 
                    }
                        break;
                default:
                    Log.l("Unkown command: " + command + " " + value,Log.LOG);
                    break;
            }
        }

    }

    public class UserEntry {
        public boolean online;
        public String username;
        UserEntry(boolean online,String username) {
            this.online = online;
            this.username = username;
        }
    }

    public boolean isUsernameValid(String username) {
        return !username.contains(":");
    }
    
    /*
    private ClientServerSocket mainServer;
    private DatagramSocket broadcastSocket = null;
    static public final int broadcastPort = 11572;

    public Net() {
        mainServer = new ClientServerSocket();
        mainServer.start();
    }

    public boolean usernameAvailable(String username) {
        return !username.isEmpty();
    }

    private void broadcast(String msg) {
        if(broadcastSocket==null) {
            try {
                broadcastSocket = new DatagramSocket(broadcastPort,InetAddress.getLocalHost());
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            try {
                broadcastSocket.setBroadcast(true);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }

        byte[] buff = new byte[256];
        buff = msg == null ? "plz connect uwu".getBytes() : msg.getBytes();
        
        InetAddress address = null;
        try {
            address = InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        
        DatagramPacket request;
        if(address!=null)
            request = new DatagramPacket(buff, buff.length, address ,broadcastPort);
        else
            return;
        try {
            broadcastSocket.send(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        broadcastSocket.close();
        //TODO: SET RECEIVING SOCKET FOR CONNECTION !
    }
         */
}
