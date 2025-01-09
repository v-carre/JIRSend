package com.JIRSendMod.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.json.simple.JSONObject;

import com.JIRSendMod.model.User;
import com.JIRSendMod.network.packets.MessageType;

import com.JIRSendMod.network.packets.BaseMessage;

/**
 * Class providing application-specific interfaces for the model to consume
 *
 * Creates a thread for the UDP server.
 * Methods are synchronous. Listeners are called from UdpServer thread. Handle with care
 * when working with a UI.
 */
public class NetworkManager {

    public static final int PORT = 1789;
    private UdpServer udpServer;
    private NetworkAgent networkAgent;
    private User user;

    private List<NetworkListener> userDiscoveryListeners;
    private List<NetworkListener> changeNameListeners;

    public NetworkManager(User user) throws SocketException {
        udpServer = new UdpServer(PORT);
        networkAgent = new NetworkAgent(PORT);

        userDiscoveryListeners = new LinkedList<NetworkListener>();
        changeNameListeners = new LinkedList<NetworkListener>();

        this.user = user;
        // listen for valid packets
        udpServer.onValidPacket(this::handleIncomingPacket);
    }

    /**
     * Starts UdpServer background thread and requests online users
     * @throws IOException
     * @throws SocketException
     */
    public void start() throws IOException {
        udpServer.start();
        // possible race condition, but very unlikely.
        // should not be useful as we send a request user discovery on creation of a new username
        try {
            networkAgent.requestUserDiscovery(user);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("NetworkManager: Failed on requesting user discovery");
        }
    }

    /**
     * Listener class for use with NetworkManager by model
     */
    public interface NetworkListener {
        /**
         * Handle incoming message. Should be cast to specific message class.
         * @param message
         */
        void handle(BaseMessage message);
    }

    public void requestChangeName(User user) {
        System.out.println("Calling requestChangeName with user: " + user);
        try {
            networkAgent.requestChangeName(user);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("NetworkManager: Failed on requesting change name");
        }
    }

    public void requestUserDiscovery(User user) {
        System.out.println("Calling requestUserDiscovery with user: " + user);
        try {
            networkAgent.requestUserDiscovery(user);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("NetworkManager: Failed on requesting user discovery");
        }
    }

    /**
     * Notify the network that the user is disconnecting, close open sockets.
     */
    public void disconnect() {
        System.out.println("Calling disconnect");
        // todo send disconnect
        udpServer.close();
        networkAgent.close();
    }

    /**
     * Allows to add an observer that listens to when a user is discovered on network.
     * Includes the behaviour related to Discover_Users and Answer_Discovery packets
     * @param listener
     */
    public void onUserDiscovered(NetworkListener listener) {
        System.out.println("Calling onUserDiscovered");
        synchronized (userDiscoveryListeners) {
            userDiscoveryListeners.add(listener);
        }
    }

    /**
     * Allows to add an observer that listens to when a user changes its name.
     * Includes the behaviour related to Change_Name packets
     * @param listener
     */
    public void onNameChanged(NetworkListener listener) {
        System.out.println("Calling onNameChanged");
        synchronized (changeNameListeners) {
            changeNameListeners.add(listener);
        }
    }

    /**
     * Propagates the update of a local username if this is applicable
     * @param user
     */
    public void updateUsername(User user) {
        System.out.println("Calling updateUsername with user: " + user);
        this.user = user;
        try {
            networkAgent.requestChangeName(user);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage() +"\nError on updateUsername method in NetworkManager");
        }
    }

    /**
     * Interpret JSON packet, send replies and notify listeners accordingly
     * @param json
     * @param sender
     */
    private void handleIncomingPacket(JSONObject json, InetAddress sender) {
        System.out.println(
            "Calling handleIncomingPacket with json: " + json + " sender: " + sender
        );
        BaseMessage message;
        try {
            // We simply drop packets received on the sender of the same packet
            /*if (this.user.getIp() != null && this.user.getIp().equals(sender)) {
                System.out.println("Packet sent and received on same agent: packet dropped...");
                return;
            } else {
            */
            message = validateIncomingPacket(json, sender);
            //}
        } catch (Exception e) {
            System.out.println("NetworkManager: Received malformed packet: " + e.getMessage());
            System.out.println(
                "NetworkManager: Malformed packet packet payload: " + json.toJSONString()
            );
            return;
        }

        switch (message.messageType()) {
            case Discover_Users:
                // reply saying we are here

                // check its not the one we sent
                if (
                    message.usernameSource().equals(user.getNickname()) ||
                    message.ipSource().equals(user.getIp())
                ) {
                    System.out.println("NetworkManager: received own discovery request");
                    break;
                }

                if (user.getNickname().isEmpty()) {
                    System.out.println(
                        "NetworkManager: Edge case : no AnswerDiscovery reply " +
                        "when user didn't register yet but started client"
                    );
                    break;
                }

                System.out.println(
                    "NetworkManager: received Discovery Request from " +
                    message.usernameSource() +
                    ", replying..."
                );

                try {
                    networkAgent.answerDiscoveryMessage(sender, user.getNickname());
                } catch (IOException e) {
                    System.out.println(
                        "NetworkManager: Failed to send reply to discovery request from " +
                        message.usernameSource() +
                        "(" +
                        sender.getHostAddress() +
                        ")"
                    );
                    e.printStackTrace();
                }

                // notify listeners
                synchronized (userDiscoveryListeners) {
                    for (NetworkListener listener : userDiscoveryListeners) listener.handle(
                        message
                    );
                }
                break;
            case Answer_Discovery:
                System.out.println(
                    "NetworkManager: received Discovery Reply from " +
                    message.usernameSource()
                );
                // just notify listeners
                synchronized (userDiscoveryListeners) {
                    for (NetworkListener listener : userDiscoveryListeners) listener.handle(
                        message
                    );
                }
                break;
            case Change_Name:
                System.out.println(
                    "NetworkManager: received Change_Name from " +
                    message.usernameSource()
                );
                // just notify listeners
                synchronized (changeNameListeners) {
                    for (NetworkListener listener : changeNameListeners) listener.handle(
                        message
                    );
                }
                break;
            default:
                break;
        }
    }

    /**
     * Validate and parse incoming packet
     *
     * Consumer can cast from BaseMessage to the class corresponding to the type
     * @param json
     * @param sender
     * @return parsed valid message
     * @throws IllegalArgumentException on invalid packet
     */
    private BaseMessage validateIncomingPacket(JSONObject json, InetAddress sender)
        throws IllegalArgumentException {
        System.out.println(
            "Calling validateIncomingPacket with json: " + json + " sender: " + sender
        );
        MessageType type = MessageType.valueOf((String) json.get("Type"));
        String usernameSource = (String) json.get("usernameSource");
        Date dateSending = new Date(Long.parseLong((String) json.get("dateSending")));

        // A packet received and sent by the same client should not raise an exception
        if (
            (type == null ||
                usernameSource == null ||
                usernameSource.length() == 0 ||
                dateSending == null) //&& this.user.getIp() != null && !this.user.getIp().equals(sender)
        ) throw new IllegalArgumentException(
            "invalid type, username source, or sent date"
        );

        return new BaseMessage(type, sender, usernameSource, dateSending);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
