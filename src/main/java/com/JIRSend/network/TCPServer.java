package com.JIRSend.network;

import java.util.Hashtable;

public class TCPServer {
    private Hashtable<String,TCPClient> ipToClient;
    public final int port;

    public TCPServer(int port) {
        ipToClient = new Hashtable<>();
        this.port = port;
    }
}
