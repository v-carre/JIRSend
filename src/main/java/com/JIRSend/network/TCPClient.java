package com.JIRSend.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
    }

    public boolean send(String string) {
        byte[] data = string.getBytes();
        try {
            sender.write(data);
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
        private boolean doRun = true;
        @Override
        public void run() {
            while (doRun) {
                // TODO meillleur condition d'arret ^^' (or not)
                try {
                    String string = receiver.readLine();
                    callback.execute(null, port, string, false, false);
                } catch (IOException e) {
                    doRun = false;
                    Log.l("Msg receiver closed for " + hostname + ":" + port);
                }
            }
        }

    }
}
