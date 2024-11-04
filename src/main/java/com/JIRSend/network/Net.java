package com.JIRSend.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.JIRSend.ui.Log;

public class Net {
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
}
