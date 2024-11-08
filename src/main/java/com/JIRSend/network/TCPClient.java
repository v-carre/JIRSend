package com.JIRSend.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import com.JIRSend.ui.Log;

public class TCPClient {
    public final String hostname;
    public final int port;
    private Socket socket;
    private OutputStream sender;
    private BufferedReader receiver;
    private NetCallback callback;
    private MessageHandlerThread thread;

    public TCPClient(String hostname, int port, NetCallback callback) {
        this.hostname = hostname;
        this.port = port;
        this.callback = callback;
        Log.l("Creating socket for "+hostname+":"+port,Log.DEBUG);
        //System.out.println("Creating socket for "+hostname+":"+port);
        try {
            socket = new Socket(hostname, port);
            sender = socket.getOutputStream();
            receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            thread = new MessageHandlerThread();
            thread.start();
        } catch (Exception e) {
            sender = null;
            receiver = null;
            thread = null;
            Log.e("Error at socket creation (" + hostname + ":" + port + "): " + e);
        }
        Log.l("Socket created "+hostname+":"+port,Log.DEBUG);
    }

    protected TCPClient(Socket socket, NetCallback callback) {
        this.hostname = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
        this.callback = callback;
        this.socket = socket;
        try {
            sender = socket.getOutputStream();
            receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            thread = new MessageHandlerThread();
            thread.start();
        } catch (IOException e) {
            sender = null;
            receiver = null;
            Log.e("Failed to create socket IO (" + hostname + ":" + port + ") " + e);
        }
    }

    public boolean send(String string) {
        byte[] data = string.getBytes();
        try {
            sender.write(data);
            sender.flush();
            return true;
        } catch (IOException e) {
            Log.e("Could not send data " + string + " to " + hostname + ":" + port);
        }
        return false;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.e("Could not close the socket to " + hostname + ":" + port);
        }
    }

    private class MessageHandlerThread extends Thread {
        @Override
        public void run() {
            while (true) {
                // TODO meilleure condition d'arret ^^' (or not)
                try {
                    String string = receiver.readLine();
                    if (string == null) {
                        Log.l("Connection ended by "+hostname+":"+port);
                        break;
                    }
                    callback.execute(socket.getInetAddress(), port, string, false, false);
                } catch (IOException e) {
                    Log.l("Msg receiver closed for " + hostname + ":" + port);
                    break;
                }
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        TCPClient client = new TCPClient("10.1.5.47",11573,new NetCallback() {
            @Override
            public void execute(InetAddress senderAddress, int senderPort, String value, boolean isBroadcast,
                boolean isUDP) {
                    System.err.println(senderAddress.toString() +" "+ senderPort +" "+ value +" "+ isBroadcast +" "+ isUDP);
                }
        });
        client.send("GetUser");
        System.out.println("sent...");
        client.thread.join();
    }
}
