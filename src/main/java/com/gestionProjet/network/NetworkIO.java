package com.gestionProjet.network;

import java.net.InetAddress;

/**
 * ioUDP
 */
public class NetworkIO {

    public static final int recvPort = 24671;
    public static final int sendPort = 24672;
    public static final int timeout = 2000; // milliseconds
    
    private UDPCallback onReceive;
    private UDPReceiver rcv;
    private UDPSenderWithAck snd;

    public NetworkIO(NetCallback callback) {
        this.onReceive = new UDPCallback();
        this.rcv = new UDPReceiver(recvPort, onReceive);
        this.snd = new UDPSenderWithAck();
    }

    protected class UDPCallback extends NetCallback {

        @Override
        public void execute(InetAddress senderAddress, int senderPort, String value) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'execute'");
        }

    }
}