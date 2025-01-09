package party.loveto.chatsystem.gui;

import java.awt.Desktop.Action;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

import com.JIRSendMod.model.User;
import com.JIRSendMod.model.UserStatus;
import com.JIRSendMod.network.DiscussionManager;
import com.JIRSendMod.network.NetworkManager;
import com.JIRSendMod.network.NetworkUtils;
import com.JIRSendMod.network.TcpClient;

public class MainController {

    /**
     * Constructor
     * Runs in Event thread
     * @param frame
     */
    protected MainController() {

        // relay username update from ui to db
        frame.onLocalUsernameUpdate(e -> {
            System.out.println("Calling onLocalUsernameUpdate");
            String newUsername = e.getActionCommand();
            System.out.println("new username:" + newUsername);
            try {
                User newUser = new User(
                    NetworkUtils.getFirstPublicIPAddress(),
                    newUsername,
                    "",
                    UserStatus.AVAILABLE
                );
                // If a username is already set then we send a change name message
                if (db.getLocalUserManager().isUsernameSet()) {
                    network.requestChangeName(newUser);
                    System.out.println("Username is set and is: " + newUser.getNickname());
                } else { // else we send a user discovery
                    network.requestUserDiscovery(newUser);
                }
                network.setUser(newUser);
                db.getLocalUserManager().updateUsername(newUsername);
                frame.setLocalUser(newUser.getNickname());

                frame.showHomescreen();
            } catch (DatabaseError error) {
                error.printStackTrace();
                frame.showErrorPopup("Failed to update local username to disk");
            } catch (SocketException error) {
                error.printStackTrace();
                frame.showErrorPopup("Failed to get a public IP address");
            }
        });

        scheduleDbLoad();
    }

    /**
     * Schedules a Swing background task to avoid loading db on event thread
     */
    private void scheduleDbLoad() {
        System.out.println("Calling scheduleDbLoad");
        // event thread
        SwingWorker<DbConnection, Void> task = new SwingWorker<DbConnection, Void>() {
            @Override
            protected DbConnection doInBackground() throws Exception {
                // background thread
                return new DbConnectionImpl();
            }

            @Override
            public void done() {
                // event thread
                try {
                    db = get();
                    System.out.println("Successfully loaded local db from disk");
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    frame.showErrorPopup("Failed to load local db: " + e.getMessage());
                    return;
                }

                // close db on window close
                frame.onWindowClose(
                    new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            // event thread
                            System.out.println("Closing down db...");
                            db.close();
                            e.getWindow().dispose();
                        }
                    }
                );

                try {
                    User localUser = new User(NetworkUtils.getFirstPublicIPAddress(), , null, null)
                    network = new NetworkManager(db.getLocalUserManager().getUser());
                    discussionManager = new DiscussionManager(
                        db.getLocalUserManager().getUser()
                    );

                    discussionManager
                        .getTcpServer()
                        .onConnexionOpened(
                            new TcpServer.ConnexionListener() {
                                @Override
                                public void handle(
                                    TcpServer.ClientHandler clientHandler
                                ) {
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        Colors.errorMessage(LOGGER, e, "sleep");
                                    }
                                    User user = null;
                                    try {
                                        user = db
                                            .getUserManager()
                                            .getUserFromIP(
                                                clientHandler
                                                    .getClientSocket()
                                                    .getInetAddress()
                                            );
                                    } catch (DatabaseError e) {
                                        Colors.errorMessage(
                                            LOGGER,
                                            e,
                                            "getting user on server connexion opening"
                                        );
                                    }
                                    frame.getUserList().getContactList().removeUser(user);
                                    user.setSocketClient(clientHandler.getClientSocket());
                                    frame.getUserList().getContactList().addUser(user);

                                    frame.getChatView().openDiscussionWith(user);

                                    clientHandler.onMessageReceived(
                                        new DiscussionListener() {
                                            @Override
                                            public void handle(String message) {
                                                System.out.println(
                                                    "Performing action due to reception of a message"
                                                );

                                                frame
                                                    .getChatView()
                                                    .displayMessage(
                                                        frame.getChatView().getUser(),
                                                        message
                                                    );
                                            }
                                        }
                                    );
                                    frame
                                        .getChatView()
                                        .addSendMessageActionListener(
                                            new ActionListener() {
                                                @Override
                                                public void actionPerformed(
                                                    ActionEvent e
                                                ) {
                                                    System.out.println(
                                                        "Performing action due to send message button"
                                                    );
                                                    String messageSent = frame
                                                        .getChatView()
                                                        .chatAreaSend();
                                                    try {
                                                        clientHandler.sendMessage(
                                                            messageSent
                                                        );
                                                    } catch (
                                                        IOException
                                                        | InterruptedException exception
                                                    ) {
                                                        Colors.errorMessage(
                                                            LOGGER,
                                                            exception,
                                                            "addSendMessageActionListener"
                                                        );
                                                    }
                                                    User user = null;
                                                    try {
                                                        user = db
                                                            .getUserManager()
                                                            .getUserFromIP(
                                                                clientHandler
                                                                    .getClientSocket()
                                                                    .getInetAddress()
                                                            );
                                                    } catch (DatabaseError exception) {
                                                        Colors.errorMessage(
                                                            LOGGER,
                                                            exception,
                                                            "getting user on server connexion opening"
                                                        );
                                                    }
                                                    System.out.println(
                                                        "Message to " +
                                                        user.getNickname() +
                                                        " sent"
                                                    );
                                                }
                                            }
                                        );
                                }
                            }
                        );

                    networkGUIBehaviour();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    frame.showErrorPopup(
                        "Failed to start network manager: " + e1.getMessage()
                    );
                    return;
                } catch (DatabaseError e2) {
                    e2.printStackTrace();
                    frame.showErrorPopup("Failed to get local user: " + e2.getMessage());
                }

                // set next screen
                try {
                    if (db.getLocalUserManager().isUsernameSet()) {
                        frame.showHomescreen();
                        frame.setLocalUser(
                            db.getLocalUserManager().getUser().getNickname()
                        );
                    } else frame.showUsernameUpdateForm();
                } catch (DatabaseError e) {
                    e.printStackTrace();
                    frame.showErrorPopup(
                        "Failed to check whether local username was set: " +
                        e.getMessage()
                    );
                }
            }
        };

        task.execute();
    }

    /*
     * Add a few listeners necessary for gui behaviour
     */
    public void networkGUIBehaviour() {
        network.onUserDiscovered(message -> {
            System.out.println("Calling onUserDiscovered");
            try {
                User newUser = new User(
                    message.ipSource(),
                    message.usernameSource(),
                    "",
                    UserStatus.AVAILABLE
                );
                System.out.println("trying to add user with IP: " + newUser.getIp());
                if (!newUser.getIp().equals(network.getUser().getIp())) {
                    db.getUserManager().safeCreateUser(newUser);
                    if (
                        !frame
                            .getUserList()
                            .getContactList()
                            .isUserPresent(newUser.getIp())
                    ) {
                        frame.addUser(newUser);
                    } else {
                        frame.getUserList().getContactList().updateUsername(newUser);
                    }
                }
            } catch (DatabaseError error) {
                error.printStackTrace();
                frame.showErrorPopup("Failed to create user in db");
            }
        });

        network.onNameChanged(message -> {
            System.out.println("Calling onNameChanged");
            try {
                User newUser = new User(
                    message.ipSource(),
                    message.usernameSource(),
                    "",
                    UserStatus.AVAILABLE
                );
                User oldUser = db.getUserManager().getUserFromIP(newUser.getIp());
                System.out.println("Entering in change username: " + newUser.getNickname());
                // To update a user, we must ensure the new username is available and the user already exists in db
                if (
                    db.getUserManager().isUsernameAvailable(newUser.getNickname()) &&
                    db.getUserManager().userExists(newUser.getIp()) &&
                    !newUser.getIp().equals(network.getUser().getIp())
                ) {
                    db.getUserManager().updateUser(newUser);
                    frame.getUserList().getContactList().updateUsername(newUser);
                    // frame.removeUser(oldUser);
                    // frame.addUser(newUser);
                    System.out.println("Username changed: " + newUser.getNickname());
                }
            } catch (DatabaseError error) {
                error.printStackTrace();
                frame.showErrorPopup("Failed to create user in db");
            }
        });

        // Add an ActionListener that listens whenever updateContactList button from
        // UserList is clicked.
        frame
            .getUserList()
            .addActionListenerToButton(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Performing action due to user list button");
                        try {
                            network.requestUserDiscovery(
                                db.getLocalUserManager().getUser()
                            );
                        } catch (DatabaseError error) {
                            error.printStackTrace();
                            frame.showErrorPopup("Failed to create user in db");
                        }
                    }
                }
            );

        frame
            .getUserList()
            .addUserClicked(
                new UserList.UserClickListener() {
                    @Override
                    public void onUserClicked(User user) {
                        // update discussion window
                        frame.getChatView().openDiscussionWith(user);

                        frame
                            .getChatView()
                            .addSendMessageActionListener(
                                new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        System.out.println(
                                            "Performing action due to send message button"
                                        );
                                        String messageSent = frame
                                            .getChatView()
                                            .chatAreaSend();
                                        try {
                                            discussionManager
                                                .getTcpClient()
                                                .sendMessage(messageSent);
                                        } catch (
                                            InterruptedException | IOException exception
                                        ) {
                                            Colors.errorMessage(
                                                LOGGER,
                                                exception,
                                                "sending message on addSendMessageActionListener"
                                            );
                                        }
                                        System.out.println(
                                            "Message to " + user.getNickname() + " sent"
                                        );
                                    }
                                }
                            );

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
                                            "Performing action due to reception of a message"
                                        );

                                        frame
                                            .getChatView()
                                            .displayMessage(
                                                frame.getChatView().getUser(),
                                                message
                                            );
                                    }
                                }
                            );
                    }
                }
            );
    }
}
