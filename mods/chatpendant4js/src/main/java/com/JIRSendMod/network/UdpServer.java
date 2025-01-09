package com.JIRSendMod.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Listens for JSON datagrams on an address-port pair
 *
 * Malformed packets are dropped
 */
public class UdpServer extends Thread {

    private DatagramSocket socket;
    private byte buffer[];
    private boolean running;
    private List<JsonPacketListener> listeners;

    /**
     * Instantiate UdpServer
     * @param port
     * @throws SocketException For example if the address-port pair is already in use
     */
    protected UdpServer(int port) throws SocketException {
        socket = new DatagramSocket(port);
        buffer = new byte[1024];
        running = false;
        listeners = new LinkedList<JsonPacketListener>();
        // timeout after 100ms, so we can close the server cleanly at most 100ms after close() is called
        socket.setSoTimeout(100);
    }

    /**
     * Helper listener for whenever a Json packet is received
     */
    public interface JsonPacketListener {
        void handle(JSONObject json, InetAddress source);
    }

    @Override
    public void run() {
        running = true;
        while (running) {
            // receive packet
            DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(inPacket); // blocking, until timeout
            } catch (SocketTimeoutException e) {
                // run another iteration, so we can check whether running has changed
                continue;
            } catch (IOException e) {
                System.out.println(
                    "UdpServer: Unexpected IOException while listening on " +
                    socket.getLocalAddress().getHostAddress() +
                    ":" +
                    socket.getLocalPort() +
                    " : " +
                    e
                );
            }

            InetAddress senderAddress = inPacket.getAddress();
            int senderPort = inPacket.getPort();
            String message = new String(inPacket.getData(), 0, inPacket.getLength());
            System.out.println(
                "UdpServer: Message received: " +
                senderAddress.getHostAddress() +
                ":" +
                senderPort +
                ", " +
                message
            );

            // parse JSON
            JSONObject data;
            try {
                data = (JSONObject) new JSONParser().parse(message);

                synchronized (listeners) {
                    for (JsonPacketListener listener : listeners) {
                        listener.handle(data, senderAddress);
                    }
                }
            } catch (ParseException e) {
                System.out.println(
                    "UdpServer: Failed to parse json from incoming datagram"
                );
                e.printStackTrace();
                continue;
            }
        }
        socket.close();
    }

    /**
     * Closes the server after at most 100ms
     */
    public void close() {
        running = false;
    }

    // TODO: add synchronized
    /**
     * Listen to when a valid JSON packet is received
     * @param listener
     */
    public void onValidPacket(JsonPacketListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }
}
