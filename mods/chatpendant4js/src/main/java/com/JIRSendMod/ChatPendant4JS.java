package com.JIRSendMod;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.ImageIcon;
import com.JIRSendAPI.*;
import com.JIRSendAPI.ModUser.Status;
import com.JIRSendMod.model.User;
import com.JIRSendMod.model.UserStatus;
import com.JIRSendMod.network.DiscussionManager;
import com.JIRSendMod.network.NetworkManager;
import com.JIRSendMod.network.NetworkUtils;
import com.JIRSendMod.network.TcpClient;
import com.JIRSendMod.network.TcpServer;
import com.JIRSendMod.network.TcpServer.DiscussionListener;

public class ChatPendant4JS implements JIRSendMod {
    private ModController controller;
    private NetworkManager network;
    private DiscussionManager discussionManager;

    private String getTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    private final JIRSendModInformation MOD_INFO = new JIRSendModInformation(
            "chatpendant4js",
            "ChatPendant for JIRSend",
            "A mod to talk with ChatPendant from JIRSend",
            "MagicTINTIN", // author
            1, // interface version
            0, // mod version
            new ImageIcon(getClass().getResource("/assets/chatpendant.png")));

    @Override
    public void initialize(ModController controller) {
        this.controller = controller;
        User localUser = new User(NetworkUtils.getFirstPublicIPAddress(), controller.mainController.getUsername(), "", null);
        try {
            network = new NetworkManager(db.getLocalUserManager().getUser());
            discussionManager = new DiscussionManager(
                    db.getLocalUserManager().getUser());

            discussionManager
                    .getTcpServer()
                    .onConnexionOpened(
                            new TcpServer.ConnexionListener() {
                                @Override
                                public void handle(
                                        TcpServer.ClientHandler clientHandler) {
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        // Colors.errorMessage(LOGGER, e, "sleep");
                                    }
                                    User user = null;
                                    try {
                                        user = db
                                                .getUserManager()
                                                .getUserFromIP(
                                                        clientHandler
                                                                .getClientSocket()
                                                                .getInetAddress());
                                    } catch (DatabaseError e) {
                                        Colors.errorMessage(
                                                LOGGER,
                                                e,
                                                "getting user on server connexion opening");
                                    }

                                    clientHandler.onMessageReceived(
                                            new DiscussionListener() {
                                                @Override
                                                public void handle(String message) {
                                                    System.out.println(
                                                            "Performing action due to reception of a message");

                                                    frame
                                                            .getChatView()
                                                            .displayMessage(
                                                                    frame.getChatView().getUser(),
                                                                    message);
                                                }
                                            });
                                }
                            });

            networkUpdateBehaviour();
        } catch (IOException e1) {
            e1.printStackTrace();
            controller.mainController.signalError("Failed to start network manager: " + e1.getMessage());
            return;
        }
        try {
            network.start();
        } catch (IOException e) {
            System.err.println("ERROR WHILE STARTING NETWORK");
        }
    }

    @Override
    public void stop() {
        network.disconnect();
    }

    @Override
    public JIRSendModInformation getModInformation() {
        return MOD_INFO;
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return !username.equals("Notes") && !username.equals("Note") && !username.equals("notes")
                && !username.equals("note") && username.length() <= 20 && username.matches("^[a-zA-Z0-9_-]+$");
    }

    @Override
    public void changeUsername(String username) {
    }

    @Override
    public void sendMessage(String recipientID, String message) {
        ModController.storeMessage.put(new ModMessage(MOD_INFO, "local", controller.mainController.getUsername(),
                "notes", message, getTime(), false));
        discussionManager
                .getTcpClient()
                .sendMessage(message);
    }

    @Override
    public void connected() {
    }

    public ModUser.Status convertStatus(UserStatus status) {
        switch (status) {
            case AVAILABLE:
                return ModUser.Status.Online;
            case IDLE:
                return ModUser.Status.Offline;
            case DO_NOT_DISTURB:
                return ModUser.Status.Busy;
            case UNKNOWN:
                return ModUser.Status.Away;

            default:
                return ModUser.Status.Online;
        }
    }

    /*
     * Add a few listeners on network
     */
    public void networkUpdateBehaviour() {
        network.onUserDiscovered(message -> {
            System.out.println("Calling onUserDiscovered");
            User newUser = new User(
                    message.ipSource(),
                    message.usernameSource(),
                    "",
                    UserStatus.AVAILABLE);
            System.out.println("trying to add user with IP: " + newUser.getIp());
            if (!newUser.getIp().equals(network.getUser().getIp())
                    && controller.mainController.isUsernameAvailable(newUser.getNickname(), MOD_INFO)) {
                ModController.contactChange.put(new ModUser(MOD_INFO, newUser.getIp().getHostAddress(),
                        newUser.getNickname(), convertStatus(newUser.getStatus())));
                // db.getUserManager().safeCreateUser(newUser);
                // if (
                // !frame
                // .getUserList()
                // .getContactList()
                // .isUserPresent(newUser.getIp())
                // ) {
                // frame.addUser(newUser);
                // } else {
                // frame.getUserList().getContactList().updateUsername(newUser);
                // }
            }
        });

        network.onNameChanged(message -> {
            System.out.println("Calling onNameChanged");
            User newUser = new User(
                    message.ipSource(),
                    message.usernameSource(),
                    "",
                    UserStatus.AVAILABLE);
            System.out.println("Entering in change username: " + newUser.getNickname());
            if (controller.mainController.isUsernameAvailable(newUser.getNickname(), MOD_INFO)) {
                ModController.contactChange.put(new ModUser(MOD_INFO, newUser.getIp().getHostAddress(),
                        newUser.getNickname(), convertStatus(newUser.getStatus())));
            }
        });

        
                                                            discussionManager
                                                                    .getTcpClient()
                                                                    .sendMessage(messageSent);
                                                        } catch (
                                                                InterruptedException | IOException exception) {
                                                            Colors.errorMessage(
                                                                    LOGGER,
                                                                    exception,
                                                                    "sending message on addSendMessageActionListener");
                                                        }
                                                        System.out.println(
                                                                "Message to " + user.getNickname() + " sent");
                                                    }
                                                });

                                discussionManager.startDiscussion(user);
                                System.out.println("Connexion with " + user + "established");

                                discussionManager
                                        .getTcpClient()
                                        .getClientHandler()
                                        .onMessageReceived(
                                                new TcpClient.DiscussionListener() {
                                                    @Override
                                                    public void handle(String message) {
                                                        System.out.println(
                                                                "Performing action due to reception of a message");

                                                        frame
                                                                .getChatView()
                                                                .displayMessage(
                                                                        frame.getChatView().getUser(),
                                                                        message);
                                                    }
                                                });
                            }
                        });
    }
}
