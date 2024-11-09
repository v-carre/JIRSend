package com.JIRSend.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import com.JIRSend.ui.Log;

public class TCPServer {
    private Hashtable<String, TCPClient> table;
    public final int port;
    private final ServerThread server;
    private final NetCallback callback;

    public TCPServer(int port, NetCallback callback) {
        table = new Hashtable<>();
        this.port = port;
        this.callback = callback;
        server = new ServerThread();
        server.start();
    }

    public boolean send(String address, String string) {
        //FIXME not working when connecting to new user
        if (table.contains(address))
            return table.get(address).send(string);
        table.put(address, new TCPClient(address, port, callback));
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
            } catch (IOException e) {
                Log.e("Error in server socket creation: " + e);
            }
            while (true) {
                try {
                    TCPClient newClient = new TCPClient(socket.accept(), callback);
                    table.put(newClient.hostname, newClient);
                } catch (IOException e) {
                    Log.l("Server socket closed (probably)", Log.LOG);
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private static class TestServerReceiver {
        public TestServerReceiver(Socket socket) throws IOException {
            var sender = new PrintStream(socket.getOutputStream(),true);
            var receiver = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println(receiver.readLine());
            sender.println("REPONSE");
            socket.close();
        }
    }
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(11573);
        while(true) {
            new TCPClient(server.accept(),new NetCallback() {

                @Override
                public void execute(InetAddress senderAddress, int senderPort, String value, boolean isBroadcast,
                        boolean isUDP) {
                    System.err.println(senderAddress.toString()+" "+senderPort+" "+value);
                    
                }
            });
        }
    }
}
