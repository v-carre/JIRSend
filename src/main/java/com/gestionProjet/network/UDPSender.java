package com.gestionProjet.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.time.Instant;

import com.gestionProjet.ui.Log;

public class UDPSender {
    /**
     * Raw sender
     * 
     * @warning no header !
     * 
     * @param destAddress
     * @param destPort
     * @param message
     */
    protected void send(InetAddress destAddress, int destPort, String message) {
        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, destAddress, destPort);

            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean sendAndWaitForAck(String destAddressName, int destPort, String value, int timeout, int maxTries) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(timeout);

            // create message with sent timestamp

            String timestamp = String.valueOf(Instant.now().toEpochMilli());
            InetAddress receiverAddress = InetAddress.getByName(destAddressName);
            String message = NetworkIO.APP_HEADER + "M<" + timestamp + ":" + value;
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, receiverAddress, destPort);

            // Wait for ACK
            byte[] ackBuffer = new byte[1024];
            DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);

            boolean ackReceived = false;
            int tries = 0;
            while (!ackReceived && tries < maxTries) {

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
            Log.l("No ack received");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
