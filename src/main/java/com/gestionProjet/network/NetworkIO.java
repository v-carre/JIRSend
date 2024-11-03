package com.gestionProjet.network;

import java.net.InetAddress;

/**
 * ioUDP is a package that commmunicates in UDP with recovery of losses
 * 
 * Communication template
 * 
 * -JIRSENDPACK>$TYPE$<$TIMESTAMP$:$PAYLOAD$
 * $TYPE$ = (A:Ack|B:Broadcast|M:Message)
 */
public class NetworkIO {
    public static final String APP_HEADER = "-JIRSENDPACKET>";
    public static final int RECV_PORT = 24671;
    // public static final int sendPort = 24672;
    public static final int TIMEOUT = 2000; // milliseconds
    public static final int MAX_TRIES = 10;

    private final UDPCallback onReceive = new UDPCallback();
    private final UDPReceiver rcv;
    private final UDPSender snd;

    public NetworkIO(NetCallback callback) {
        this.rcv = new UDPReceiver(RECV_PORT, onReceive);
        this.snd = new UDPSender();
        this.rcv.start();
    }

    public boolean send(String destAddress, String value) {
        return snd.sendAndWaitForAck(destAddress, RECV_PORT, value, TIMEOUT, MAX_TRIES);
    }

    protected class UDPCallback extends NetCallback {

        @Override
        public void execute(InetAddress senderAddress, int senderPort, String value) {
        }

    }

    private void sendAck(InetAddress sAddress, int sPort, String rcvdHeader) {
        snd.send(sAddress, sPort, rcvdHeader);
    }
}