package com.JIRSend.model.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Hashtable;

import com.JIRSend.view.cli.Log;

public class TCPServer {
    private Hashtable<String, TCPClient> table;
    public final int port;
    private final ServerThread server;
    private final NetCallback callback;
    private final VoidCallback onRunning;

    public TCPServer(int port, NetCallback callback, VoidCallback onRunning) {
        table = new Hashtable<>();
        this.port = port;
        this.callback = callback;
        this.onRunning = onRunning;
        server = new ServerThread();
        server.start();
    }

    public boolean send(String address, String string) {
        if (table.contains(address))
            return table.get(address).send(string);

        TCPClient newClient = new TCPClient(address, port, callback);
        if (newClient.hasFailedToStart())
            return false;
        table.put(address, newClient);
        return table.get(address).send(string);
    }

    public void stop() {
        try {
            server.socket.close();
        } catch (IOException e) {
            Log.e("Failed to close server socket" + e);
        }
    }

    private class ServerThread extends Thread {
        public ServerSocket socket;

        @Override
        public void run() {
            try {
                socket = new ServerSocket(port);
                onRunning.execute();
            } catch (IOException e) {
                Log.e("Error in server socket creation: " + e);
            }
            if (socket == null)
                return;
            while (true) {
                try {
                    TCPClient newClient = new TCPClient(socket.accept(), callback);
                    if (newClient.hasFailedToStart())
                        continue;
                    table.put(newClient.hostname, newClient);
                } catch (IOException e) {
                    Log.l("Server socket closed (probably)", Log.LOG);
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
