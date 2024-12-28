package com.JIRSendApp.model.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.Instant;

import com.JIRSendApp.view.cli.Log;

public class UDPSender {

    private DatagramSocket socket;
    private InetAddress localAddress;
    private int receiversPort;

    public UDPSender(int broadcastPort, int receiversPort) throws SocketException {
        // while (true) {
        // try {
        // this.socket = new DatagramSocket(broadcastPort);
        // this.socket.setBroadcast(true);
        // this.localAddress = InetAddress.getByName("255.255.255.255");
        // this.receiversPort = receiversPort;
        // } catch (Exception e) {
        // Log.e("Failed to create UDP Sender: " + e);
        // }

        // if (this.socket != null)
        // break;
        // }
        // try {
        // this.socket = new DatagramSocket(broadcastPort);
        // this.socket.setBroadcast(true);
        // this.localAddress = InetAddress.getByName("255.255.255.255");
        // this.receiversPort = receiversPort;
        // } catch (Exception e) {
        // e.printStackTrace();
        // Log.e("Failed to create UDP Sender: " + e);
        // }
        this.socket = new DatagramSocket(broadcastPort);
        this.socket.setBroadcast(true);
        try {
            this.localAddress = InetAddress.getByName("255.255.255.255");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.receiversPort = receiversPort;
    }

    public void stop() {
        this.socket.close();
    }

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
            String message = NetworkIO.APP_HEADER + "M<" + timestamp + "|" + value;
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
                try {
                    socket.receive(ackPacket);
                    return true;
                } catch (SocketTimeoutException e) {
                    Log.l("No ACK received within the timeout period.", Log.WARNING);
                }
            }
            Log.l("No ack received", Log.ERROR);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void broadcastNoHeader(String value) {
        String message = value;
        byte[] buffer = message.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, localAddress, this.receiversPort);
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            Log.l("Failed to send broadcast msg: " + e);
        }
    }

    public void broadcast(String value) {
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String message = NetworkIO.APP_HEADER + "B<" + timestamp + "|" + value;

        broadcastNoHeader(message);
    }
}
