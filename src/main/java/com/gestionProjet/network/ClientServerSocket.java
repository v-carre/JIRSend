package com.gestionProjet.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import com.gestionProjet.ui.Log;

public class ClientServerSocket extends Thread {
    private ServerSocket serverSocket;
    public final int defaultPort = 12971;
    private int port = defaultPort;
    private ArrayList<ConnectionHandler> clients = new ArrayList<ConnectionHandler>();

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            Log.l("Invalid port number: " + port + " -> " + e.toString(), Log.ERROR);
        }
        try {
            while (true) {
                var client = new ConnectionHandler(serverSocket.accept());
                clients.add(client);
                client.start();
            }
        } catch (IOException e) {
            Log.l("[ERR] could not accept connection." + e, Log.ERROR);
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void close() {
        try {
            serverSocket.close();
        } catch (Exception e) {
            Log.l("[LOG] " + e, Log.ERROR);
        }
    }

    public int getPort() {
        return port;
    }
}
