package com.gestionProjet.network;

import com.gestionProjet.ui.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Net {
    private MainServer mainServer;
    public Net() {
        mainServer = new MainServer();
        mainServer.start();
    }

    public boolean usernameAvailable(String username) {
        return !username.isEmpty();
    }

    private void broadcast(String msg) {

    }

    private class MainServer extends Thread {
        private ServerSocket serverSocket;
        private int port = 12971;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                Log.l("[ERR] invalid port number " + port + "\nTry with another port.", Log.ERROR);
            }
            try {
                while (true) {
                    new ClientHandler(serverSocket.accept()).start();
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

        public int getPort() { return port; }

        private static class ClientHandler extends Thread {
            private final Socket socket;
            private BufferedReader in;
            private PrintWriter out;

            public ClientHandler(Socket socket) {
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
    }
}
