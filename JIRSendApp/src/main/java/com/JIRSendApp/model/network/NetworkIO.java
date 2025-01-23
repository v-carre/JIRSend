package com.JIRSendApp.model.network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.JIRSendApp.view.cli.Log;

/**
 * ioUDP is a package that commmunicates in UDP with recovery of losses
 * 
 * Communication template
 * 
 * -JIRSend>$<$|$
 * $ = (A:Ack|B:Broadcast|M:Message)
 */
public class NetworkIO {
    // set to false to use UDP with ACK messages instead of TCP
    public static final boolean NO_ACK = true;
    public static final boolean NO_HEADER = false;
    public static final String APP_HEADER = "-JIRSend>";
    public static final int RECV_PORT = 11572;
    public static final int TCP_PORT = 11573;
    public static final int BRDC_PORT = 11574;
    public static final int RECV_PORT_TEST = 10572;
    public static final int TCP_PORT_TEST = 10573;
    public static final int BRDC_PORT_TEST = 10574;
    public static final int TIMEOUT = 500; // milliseconds
    public static final int MAX_TRIES = 10;

    private final UDPCallback onReceive = new UDPCallback();
    private final UDPReceiver RCV;
    private final UDPSender SND;
    private final NetCallback callback;
    private final VoidCallback onRunning;
    private final TCPServer TCP_SERVER;
    private final boolean IS_TEST;

    public NetworkIO(NetCallback callback, VoidCallback onRunning, boolean test) throws SocketException {
        this.IS_TEST = test;
        this.RCV = new UDPReceiver(test ? RECV_PORT_TEST : RECV_PORT, onReceive);
        this.RCV.start();
        this.onRunning = onRunning;
        this.callback = callback;
        TCP_SERVER = new TCPServer(test ? TCP_PORT_TEST : TCP_PORT, this.callback, this.onRunning);
        this.SND = new UDPSender(test ? BRDC_PORT_TEST : BRDC_PORT, test ? RECV_PORT_TEST : RECV_PORT);
    }

    public NetworkIO(NetCallback callback, VoidCallback onRunning) throws SocketException {
        this(callback, onRunning, false);
    }

    public void stop() {
        this.TCP_SERVER.stop();
        this.RCV.stop();
        this.SND.stop();
    }

    public boolean send(String destAddress, String value) {
        return TCP_SERVER.send(destAddress, value);
    }

    public boolean sendUDP(String destAddress, String value) {
        if (NO_ACK) {
            try {
                SND.send(InetAddress.getByName(destAddress), IS_TEST ? RECV_PORT_TEST : RECV_PORT, value);
            } catch (UnknownHostException e) {
                return false;
            }
            return true;
        } else
            return SND.sendAndWaitForAck(destAddress, IS_TEST ? RECV_PORT_TEST : RECV_PORT, value, TIMEOUT, MAX_TRIES);
    }

    public void broadcast(String message) {
        if (NO_HEADER)
            SND.broadcastNoHeader(message);
        else
            SND.broadcast(message);
    }

    protected class UDPCallback extends NetCallback {
        @Override
        public void execute(InetAddress senderAddress, int senderPort, String value, boolean isBroadcast,
                boolean isUDP) {

            // Do not try to parse a non-existant header
            if (NO_HEADER) {
                callback.execute(senderAddress, senderPort, value, false, true);
                return;
            }

            String[] messageParts = value.split("\\|", 2);
            // if the message is not a valid request
            if (!value.startsWith(APP_HEADER) || messageParts.length != 2
                    || messageParts[0].split("\\<", 2).length != 2) {
                Log.l("Received an unknown message '" + value + "' sent by " + senderAddress.getHostAddress() + ":"
                        + senderPort + "");
                return;
            }
            // if we receive an ack we ignore it
            if (messageParts[0].startsWith(APP_HEADER + "A<")) {
                return;
            }
            // if we receive a broadcasted message
            if (messageParts[0].startsWith(APP_HEADER + "B<")) {
                callback.execute(senderAddress, senderPort, messageParts[1], true, true);
                return;
            }
            if (!NO_ACK)
                sendAck(senderAddress, senderPort, messageParts[0]);
            callback.execute(senderAddress, senderPort, messageParts[1], false, true);
        }

    }

    private void sendAck(InetAddress sAddress, int sPort, String rcvdHeader) {
        String sentTimestamp = rcvdHeader.split("\\<", 2)[1];

        try {
            String ackMsg = APP_HEADER + "A<" + sentTimestamp + "|" + Inet4Address.getLocalHost().getHostAddress();
            SND.send(sAddress, sPort, ackMsg);
        } catch (UnknownHostException e) {
            Log.e("Could not send Ack");
        }
    }
}