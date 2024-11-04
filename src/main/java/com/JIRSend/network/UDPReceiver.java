package com.JIRSend.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import com.JIRSend.ui.Log;

public class UDPReceiver {
    private final NetCallback callback;
    private final int port;
    private DatagramSocket socket;
    private Thread rcvThread;
    private boolean isRunning;
    private final String local;

    public UDPReceiver(int port, NetCallback callback) {
        this.port = port;
        this.callback = callback;
        this.isRunning = false;
        this.local = getLocalAddr();
    }

    public void start() {
        this.isRunning = true;
        this.rcvThread = new Thread(() -> {
            Log.l("Listening on port " + port + "...", Log.LOG);
            recverLoop();
        });
        this.rcvThread.start();
    }

    public void stop() {
        this.isRunning = false;
        try {
            rcvThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void recverLoop() {
        try {
            this.socket = new DatagramSocket(port);

            byte[] receiveBuffer = new byte[1024];

            while (isRunning) {
                // Receive message
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());

                if (receivePacket.getAddress().getHostAddress().equals(local)) {
                    Log.l("Received message from self",Log.DEBUG);
                    continue;
                }
                
                //System.out.println("Received message: " + message);
                
                // broadcast is false only because it is not seen at the
                callback.execute(receivePacket.getAddress(), receivePacket.getPort(), message, false);

                // String ack = "ACK: " + message;
                // byte[] ackBuffer = ack.getBytes();
                // InetAddress senderAddress = receivePacket.getAddress();
                // int senderPort = receivePacket.getPort();
                // DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length,
                // senderAddress, senderPort);
                // socket.send(ackPacket);
                // System.out.println("Sent ACK for message: " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getLocalAddr() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface i = interfaces.nextElement();
                if (i.isLoopback() || !i.isUp())
                    continue;
                Enumeration<InetAddress> addresses = i.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!(addr instanceof Inet4Address))
                        continue;
                    return addr.getHostAddress();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "127.0.0.1"; //rip...
    }
}
