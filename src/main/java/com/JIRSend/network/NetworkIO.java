package com.JIRSend.network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.JIRSend.view.cli.Log;

/**
 * ioUDP is a package that commmunicates in UDP with recovery of losses
 * 
 * Communication template
 * 
 * -JIRSENDPACK>$TYPE$<$TIMESTAMP$|$PAYLOAD$
 * $TYPE$ = (A:Ack|B:Broadcast|M:Message)
 */
public class NetworkIO {
    public static final boolean NO_HEADER_NO_ACK = true;
    public static final String APP_HEADER = "-ConnaissezVousJIRSend?>";
    public static final int RECV_PORT = NO_HEADER_NO_ACK ? 1610 : 11572;
    public static final int TCP_PORT = 11573;
    public static final int BRDC_PORT = 11574;
    public static final int TIMEOUT = 500; // milliseconds
    public static final int MAX_TRIES = 10;

    private final UDPCallback onReceive = new UDPCallback();
    private final UDPReceiver rcv;
    private final UDPSender snd;
    private final NetCallback callback;
    private final VoidCallback onRunning;
    private final TCPServer tcpServer;

    public NetworkIO(NetCallback callback, VoidCallback onRunning) {
        this.rcv = new UDPReceiver(RECV_PORT, onReceive);
        this.rcv.start();
        this.onRunning = onRunning;
        this.callback = callback;
        tcpServer = new TCPServer(TCP_PORT, this.callback, this.onRunning);
        this.snd = new UDPSender(BRDC_PORT, RECV_PORT);
    }

    public boolean send(String destAddress, String value) {
        return tcpServer.send(destAddress, value);
        //return snd.sendAndWaitForAck(destAddress, RECV_PORT, value, TIMEOUT, MAX_TRIES);
    }

    public boolean sendUDP(String destAddress, String value) {
        if(NO_HEADER_NO_ACK) {
            try {
                snd.send(InetAddress.getByName(destAddress), RECV_PORT, value);
            } catch (UnknownHostException e) {
                return false;
            }
            return true;
        }
        else
            return snd.sendAndWaitForAck(destAddress, RECV_PORT, value, TIMEOUT, MAX_TRIES);
    }

    public void broadcast(String message) {
        if(NO_HEADER_NO_ACK)
            snd.broadcastNoHeader(message);
        else
            snd.broadcast(message);
    }

    protected class UDPCallback extends NetCallback {
        @Override
        public void execute(InetAddress senderAddress, int senderPort, String value, boolean isBroadcast,boolean isUDP) {
            
            //Do not try to parse an inexistant header
            if(NO_HEADER_NO_ACK) {
                callback.execute(senderAddress, senderPort, value, false, true);
                return;
            }

            String[] messageParts = value.split("\\|", 2);
            // if the message is not a valid request
            if (!value.startsWith(APP_HEADER) || messageParts.length != 2 || messageParts[0].split("\\<", 2).length != 2) {
                Log.l("Received an unknown message '" + value + "' sent by " + senderAddress.getHostAddress() + ":" + senderPort + "");
                return;
            }
            // if we receive an ack we ignore it
            if (messageParts[0].startsWith(APP_HEADER + "A<")) {
                return;
            }
            // if we receive a broadcasted message
            if (messageParts[0].startsWith(APP_HEADER + "B<")) {
                callback.execute(senderAddress, senderPort, messageParts[1], true,true);
                return;
            }
            sendAck(senderAddress, senderPort, messageParts[0]);
            callback.execute(senderAddress, senderPort, messageParts[1], false,true);
        }

    }

    private void sendAck(InetAddress sAddress, int sPort, String rcvdHeader) {
        String sentTimestamp = rcvdHeader.split("\\<", 2)[1];

        try {
            String ackMsg = APP_HEADER + "A<" + sentTimestamp + "|" + Inet4Address.getLocalHost().getHostAddress();
            snd.send(sAddress, sPort, ackMsg);
        } catch (UnknownHostException e) {
            Log.e("Could not send Ack");
        }
    }
}