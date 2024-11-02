package com.gestionProjet.network;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPReceiver {
    private NetCallback callback;
    private int port;
    public UDPReceiver(int port, NetCallback callback) {
        this.port = port;
        this.callback = callback;
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
