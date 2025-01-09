package com.JIRSendMod;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer extends Thread {
    private volatile boolean state = true;
    private final ServerSocket socket;

    public TCPServer(){
        super();
        try {
            this.socket = new ServerSocket(1643);
        } catch (IOException e) {
            throw new RuntimeException(e); //or "e.printStackTrace();" ?
        }
    }
    @Override
    public void run() {
        try {
            listener();
        } catch (Exception e) {
            System.err.println("Critical error: Clavardon TCPServer Thread stopped: "+e);
        }
    }

    private void listener() {
        while (state) {
            try {
                Socket clientSocket = socket.accept();
                new TCPClient(clientSocket).start();
            } catch (IOException e) {
                if (!state) {
                    //System.out.println("<Clavardon-Arrêt> -> Arrêt du thread d'écoute TCP");
                } else {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopThread() {
        this.state = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
