package com.JIRSend.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.JIRSend.ui.Log;

public class UDPReceiver {
    private final NetCallback callback;
    private final int port;
    private DatagramSocket socket;
    private Thread rcvThread;
    private boolean isRunning;
    private String local; 

    public UDPReceiver(int port, NetCallback callback) {
        this.port = port;
        this.callback = callback;
        this.isRunning = false;

        try {
            InetAddress localhost = InetAddress.getLocalHost();
            this.local = (localhost.getHostAddress()).trim();
            
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
            System.out.println("Receiver is listening on port " + port);

            while (isRunning) {
                // Receive message
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received message: " + message);

                if (receivePacket.getAddress().getHostAddress().equals(local)) {
                    System.out.println("Received message from self");
                    continue;
                }

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
}
