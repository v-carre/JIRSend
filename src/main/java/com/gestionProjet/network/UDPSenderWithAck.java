package com.gestionProjet.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.time.Instant;

public class UDPSenderWithAck {
    public boolean send(String destAddressName, int destPort, String value, int timeout, int maxTries) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(timeout);

            InetAddress receiverAddress = InetAddress.getByName(destAddressName);

            // Wait for ACK
            byte[] ackBuffer = new byte[1024];
            DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);

            boolean ackReceived = false;
            int tries = 0;
            while (!ackReceived && tries < maxTries) {
                // create message with sent timestamp
                String timestamp = String.valueOf(Instant.now().toEpochMilli());
                String message = NetworkIO.APP_HEADER + "|" + timestamp + ":" + value;
                byte[] buffer = value.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, destPort);
                // Send message
                socket.send(packet);
                System.out.println("Sent message: " + message);
                try {
                    socket.receive(ackPacket);
                    String ackMessage = new String(ackPacket.getData(), 0, ackPacket.getLength());
                    System.out.println("Received ACK: " + ackMessage);
                    return true;
                } catch (SocketTimeoutException e) {
                    System.out.println("No ACK received within the timeout period.");
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
