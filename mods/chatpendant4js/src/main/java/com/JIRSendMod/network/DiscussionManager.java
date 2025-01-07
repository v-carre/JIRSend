package com.JIRSendMod.network;

import java.io.IOException;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;

import com.JIRSendMod.model.User;

public class DiscussionManager {

    private User localUser;
    public static final int SERVERPORT = 1791;

    private TcpClient tcpClient;
    private TcpServer tcpServer;
    private DiscussionAgent discussionAgent;

    public DiscussionManager(User localUser) throws SocketException {
        this.localUser = localUser;
        this.tcpServer = new TcpServer(SERVERPORT);
        this.tcpClient = new TcpClient();
        this.discussionAgent = new DiscussionAgent();
        tcpServer.start();
    }

    public void startDiscussion(User user) {
        try {
            tcpClient.startConnection(
                user.getIp().getHostName(),
                DiscussionManager.SERVERPORT
            );
        } catch (IOException e) {
            // Colors.errorMessage(LOGGER, e, "startDiscussion");
        }
    }

    /*
    public void sendMessage(String message) {
        try {
            if ("End".equals(message)) {
                tcpClient.stopConnection();
                tcpServer.stopServer();
            } else {
                tcpClient.sendMessage(message);
            }
        } catch (IOException e) {
            Colors.errorMessage(LOGGER, e, "sendMessage");
        }
    }*/

    public TcpClient getTcpClient() {
        return tcpClient;
    }

    public TcpServer getTcpServer() {
        return tcpServer;
    }
}
