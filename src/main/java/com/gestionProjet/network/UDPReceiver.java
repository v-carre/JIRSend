package com.gestionProjet.network;


import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.gestionProjet.ui.Log;

public class UDPReceiver {
    private final NetCallback callback;
    private final int port;
    private Thread rcvThread;

    public UDPReceiver(int port, NetCallback callback) {
        this.port = port;
        this.callback = callback;
    }

    public void start() {
        this.rcvThread = new Thread(() -> {
            Log.l("Listening on port " + port + "...", Log.LOG);
            recverLoop();
        });
    }

    public void stop() {
        try {
            rcvThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void recverLoop() {
        try (DatagramSocket socket = new DatagramSocket(port)) {
            byte[] receiveBuffer = new byte[1024];
            System.out.println("Receiver is listening on port " + port);

            while (true) {
                // Receive message
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received message: " + message);

                callback.execute(receivePacket.getAddress(), receivePacket.getPort(), message);
                
                // String ack = "ACK: " + message;
                // byte[] ackBuffer = ack.getBytes();
                // InetAddress senderAddress = receivePacket.getAddress();
                // int senderPort = receivePacket.getPort();
                // DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length, senderAddress, senderPort);
                // socket.send(ackPacket);
                // System.out.println("Sent ACK for message: " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
