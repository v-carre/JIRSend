package com.gestionProjet.network;

import java.net.InetAddress;

/**
 * ioUDP
 */
public class NetworkIO {
    public static final String APP_HEADER = "JIRSENDPACKET";
    public static final int RECV_PORT = 24671;
    // public static final int sendPort = 24672;
    public static final int TIMEOUT = 2000; // milliseconds
    public static final int MAX_TRIES = 10;

    private final UDPCallback onReceive = new UDPCallback();
    private final UDPReceiver rcv;
    private final UDPSenderWithAck snd;

    public NetworkIO(NetCallback callback) {
        this.rcv = new UDPReceiver(RECV_PORT, onReceive);
        this.snd = new UDPSenderWithAck();
        this.rcv.start();
    }

    public boolean send(String destAddress, String value) {
        return snd.send(destAddress, RECV_PORT, value, TIMEOUT, MAX_TRIES);
    }

    protected class UDPCallback extends NetCallback {

        @Override
        public void execute(InetAddress senderAddress, int senderPort, String value) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'execute'");
        }

    }
}