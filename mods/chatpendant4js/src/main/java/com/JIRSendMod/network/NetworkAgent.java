package com.JIRSendMod.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;

import com.JIRSendMod.model.User;
import com.JIRSendMod.model.UserStatus;
import com.JIRSendMod.network.packets.MessageType;
import com.JIRSendMod.network.packets.BaseMessage;

/**
 * Contains the UDP sending socket and packet sender methods
 */
public class NetworkAgent {

    private DatagramSocket socket;
    private static final int MAX_ACTIVE_USERS = 1000;
    private final int port;

    protected NetworkAgent(int port) throws SocketException {
        this.socket = new DatagramSocket();
        this.port = port;
        socket.setBroadcast(true);
    }

    /**
     * Close open sockets
     */
    public void close() {
        socket.close();
    }

    private void sendPacket(BaseMessage message, InetAddress address) throws IOException {
        System.out.println(
                "Calling sendPacket with message: " + message + " address: " + address);

        byte[] buffer = message.toJson().toJSONString().getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(packet);
    }

    // To be used with a broadcast address as 255.255.255.255
    private void broadcast(BaseMessage message) throws IOException {
        System.out.println("Calling broadcast with message: " + message);
        sendPacket(message, InetAddress.getByName("255.255.255.255"));
    }

    /**
     * Broadcast DiscoverUserMessage to local network
     * 
     * @throws IOException
     */
    public void requestUserDiscovery(User localUser) throws IOException {
        System.out.println("Calling requestUserDiscovery with localUser: " + localUser);
        BaseMessage message = new BaseMessage(
                MessageType.Discover_Users,
                localUser.getIp(),
                localUser.getNickname(),
                new Date());
        broadcast(message);
    }

    /**
     * Broadcast a ChangeName message which notifies all the other
     * agents on local network that the user changed its username.
     * 
     * @throws IOException
     */
    public void requestChangeName(User localUser) throws IOException {
        System.out.println("Calling requestChangeName with localUser: " + localUser);
        BaseMessage message = new BaseMessage(
                MessageType.Change_Name,
                localUser.getIp(),
                localUser.getNickname(),
                new Date());
        broadcast(message);
    }

    /**
     * Signal user app disconnection DisconnectMessage to local network
     * 
     * @throws IOException
     */
    public void signalAppDisconnect(User localUser) throws IOException {
        System.out.println("Calling signalAppDisconnect with localUser: " + localUser);
        BaseMessage message = new BaseMessage(
                MessageType.Disconnect,
                localUser.getIp(),
                localUser.getNickname(),
                new Date());
        broadcast(message);
    }

    // TODO remove this, handle discovery asynchronously
    public ArrayList<User> discoverUsers(BaseMessage discoverUserMessage, int port) {
        System.out.println("Calling signalAppDisconnect with discoverUserMessage: " +
                discoverUserMessage + " port: " + port);

        ArrayList<User> discoveredUsers = new ArrayList<User>();

        try {
            // Appeler la méthode broadcast en envoyant le JSON dans le message
            // Packet is automatically a broadcast
            broadcast(discoverUserMessage);

            socket.setSoTimeout(1000);
            byte[] bufferIn = new byte[256];
            String receivedMessage;
            String[] receivedParsedMessage;

            DatagramPacket inPacket = new DatagramPacket(bufferIn, port);
            long startTime = System.currentTimeMillis();
            long timeout = 5000; // timeout after 5 seconds
            for (int i = 0; i < MAX_ACTIVE_USERS; i++) {
                if (System.currentTimeMillis() - startTime > timeout) {
                    System.out.println("Timeout reached. Exiting the loop.");
                    break; // Exit the loop if the time limit is reached
                }
                socket.receive(inPacket);
                receivedMessage = new String(inPacket.getData(), 0, inPacket.getLength());
                receivedParsedMessage = receivedMessage.split(":");

                if (receivedParsedMessage[1].equals(
                        MessageType.Answer_Discovery.getDescription())) {
                    discoveredUsers.add(
                            new User(
                                    InetAddress.getByName(receivedParsedMessage[3]),
                                    receivedParsedMessage[5],
                                    "",
                                    UserStatus.fromDescription(receivedParsedMessage[7])));
                }
            }
            return discoveredUsers;
        } catch (IOException e) {
            e.getMessage();
            return discoveredUsers;
        }
    }

    /**
     * Build a user discovery packet and send it
     * 
     * @param ipDest
     * @param localUsername
     * @throws IOException
     */
    public void answerDiscoveryMessage(InetAddress ipDest, String localUsername)
            throws IOException {
        System.out.println("Calling signalAppDisconnect with ipDest: " + ipDest + " localUsername: " + localUsername);
        BaseMessage message = new BaseMessage(
                MessageType.Answer_Discovery,
                ipDest,
                localUsername,
                new Date());
        sendPacket(message, ipDest);
    }
    // Discover_Users,Answer_Discovery,New_User,Notify_Presence, Change_Name,
    // Disconnect

    /**
     * public ArrayList<User> updateContactList() {
     * User localUser = Client.getUser();
     * DiscoverUserMessage discoverUserMessage = new DiscoverUserMessage(
     * localUser.getIp(),
     * localUser.getNickname(),
     * new Date()
     * );
     * return discoverUsers(discoverUserMessage, Client.getUserPort());
     * }
     */
}
