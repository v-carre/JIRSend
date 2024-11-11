package com.JIRSend.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.JIRSend.ui.Log;

public class TCPClient {
    public final String hostname;
    public final int port;
    private Socket socket;
    private PrintWriter sender;
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
            // sender = socket.getOutputStream();
            sender = new PrintWriter(socket.getOutputStream(), true);
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
            // sender = socket.getOutputStream();
            sender = new PrintWriter(socket.getOutputStream(), true);
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
        // sender.write(data);
        // sender.flush();
        sender.println(string);
        return true;
        // return false;
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
            this.setName(hostname + "-ClientHandler");
            Log.e("NTM ?" + hostname + ":" + port);
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

    public static void main(String[] args) throws InterruptedException, UnknownHostException, IOException {
        //TCPClient client = new TCPClient("10.1.5.47",11573,new NetCallback() {
        //    @Override
        //    public void execute(InetAddress senderAddress, int senderPort, String value, boolean isBroadcast,
        //        boolean isUDP) {
        //            System.err.println(senderAddress.toString() +" "+ senderPort +" "+ value +" "+ isBroadcast +" "+ isUDP);
        //        }
        //});
        //client.send("GetUser");
        //System.out.println("sent...");
        //client.thread.join();
        Socket socket = new Socket("10.1.5.44", 11573);
        System.out.println("created");
        var sender = new PrintWriter(socket.getOutputStream(),true);
        sender.println("GetUser");
        System.out.println("sent");
        var recv = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String string = "";
        while(string != null) {
            System.out.println("receiving");
            string = recv.readLine();
            System.out.println(string);
        }
        socket.close();
    }
}
