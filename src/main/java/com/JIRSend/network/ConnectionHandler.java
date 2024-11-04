package com.JIRSend.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.JIRSend.ui.Log;

public class ConnectionHandler extends Thread {
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ConnectionHandler(Socket socket) {
        this.socket = socket;
        Log.l("New client connected: " + socket.toString(), Log.LOG);
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                String str = in.readLine();
                String[] parsedMsg = str.split(" ");
                Log.l("Msg received from " + socket.getInetAddress().toString() + ": " + str, Log.LOG);
                switch (parsedMsg[0]) {
                    default -> Log.l("Unkown command: " + parsedMsg[0]);
                }
            }

        } catch (Exception e) {
            Log.l("Exception in ClientHandler: " + e, Log.ERROR);
        }
    }
}
