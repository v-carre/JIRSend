package com.JIRSend.network;

import java.net.InetAddress;

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
*      - UpdateUsername old:new
*      - SendMessage username:msg
 */
public class Net {
    private NetworkIO netIO;


    public Net() {
        netIO = new NetworkIO(new NetworkCallback());
        netIO.broadcast("GetUser");
    }

    public boolean usernameAvailable(String username) {
        if (username.contains(":"))
            return false;
        netIO.broadcast("NewUser "+username);
        return true;
    }

    private class NetworkCallback extends NetCallback {
        @Override
        public void execute(InetAddress senderAddress, int senderPort, String value, boolean isBroadcast) {
            Log.l("[" + senderAddress.getHostAddress() + ":" + senderPort + "] " + value,Log.LOG);
        }
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
