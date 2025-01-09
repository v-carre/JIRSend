package com.JIRSendMod;

import java.util.ArrayList;

import com.JIRSendAPI.ModController;
import com.JIRSendAPI.ModMessage;
import com.JIRSendAPI.ModUser;

public class UserList implements UDPServer.Observer, TCPClient.Observer {
    private static final UserList instance = new UserList();
    private ArrayList<User> arrayUsers;
    private User me = null;
    private ModController controller = null;
    private final ArrayList<Observer> observers = new ArrayList<>();

    public interface Observer {
        void updateUserList(String type, String[] args);
    }

    public void subscribe(Observer observer) {
        this.observers.add(observer);
    }

    static public void setInstanceController(ModController c) {
        instance.controller = c;
    }

    private UserList() {
        this.arrayUsers = new ArrayList<>(); // bdd.getAllUsers();
        // try {
        // this.bdd = new BDDInterface();
        // this.arrayUsers = new ArrayList<>(); // bdd.getAllUsers();
        // User me = this.getUserByIpAddress(MyNetworkInterface.getIpAddr());
        // if (me != null) {
        // this.me = me;
        // this.me.setState(true);
        // this.arrayUsers.remove(me);
        // }
        // ArrayList<ModMessage> messages = bdd.getAllMessages();
        // for (ModMessage message : messagthis.lists(this.me))
        // user = this.getUserByIpAddress(message.getSender().getIpAddress());
        // else
        // user = this.getUserByIpAddress(message.getReceiver().getIpAddress());
        // if (user != null)
        // user.addMessage(message);
        // }
        // } catch (DatabaseCreationException | SQLException e) {
        // LOGGER.info("Mode without BDD");
        // this.bdd = null;
        // this.arrayUsers = new ArrayList<>();
        // }
    }

    public void UserListDel() {
        for (User user : this.arrayUsers)
            if (user.getSocket() != null)
                user.getSocket().stopThread();
        // if (this.bdd != null) {
        // ArrayList<User> users = new ArrayList<>(this.arrayUsers);
        // users.add(this.me);
        // this.bdd.saveUsers(users);
        // for (User user : this.arrayUsers)
        // this.bdd.saveMessages(user.getMessages());
        // this.bdd.BDDInterfaceDel();
        // }
    }

    public static UserList getInstance() {
        return instance;
    }

    public boolean usernameIsAvailable(String username) {
        if (controller == null)
            return false;
        for (User user : arrayUsers) if(user.getUsername().equals(username)){
            return false;
        }
        return controller.mainController.isUsernameAvailable(username, Clavardons4JS.MOD_INFO);
    }

    public void addUser(User user) {
        if (me != null && user.getUsername().equals(me.getUsername()))
            return;
        for (User thisUser : this.arrayUsers) {
            if (thisUser.getIpAddress().equals(user.getIpAddress())) {
                if (!thisUser.isActive()) {
                    thisUser.setActive(true);
                    thisUser.modUser = new ModUser(thisUser.modUser.mod, thisUser.modUser.userID,
                            thisUser.modUser.username, ModUser.Status.Online);
                }
                if (!thisUser.getUsername().equals(user.getUsername())) {
                    thisUser.setUsername(user.getUsername());
                    thisUser.setChange(true);
                }
                for (Observer observer : this.observers)
                    observer.updateUserList("addUser", new String[] { "DA", thisUser.getUsername() });
                return;
            }
        }
        arrayUsers.add(user);
        for (Observer observer : this.observers)
            observer.updateUserList("addUser", new String[] { "NA", user.getUsername() });
    }

    public void addUsers(ArrayList<User> users) {
        for (User user : users) {
            addUser(user);
        }
    }

    public void setUserOnline(String ipAddress) {
        for (User user : this.arrayUsers)
            if (user.getIpAddress().equals(ipAddress)) {
                user.setActive(true);
                user.modUser = new ModUser(user.modUser.mod, user.modUser.userID, user.modUser.username,
                        ModUser.Status.Online);
            }
    }

    public void setUserOffline(String username) {
        for (User user : this.arrayUsers)
            if (user.getUsername().equals(username) && user.isActive()) {
                user.setActive(false);
                user.modUser = new ModUser(user.modUser.mod, user.modUser.userID, user.modUser.username,
                        ModUser.Status.Offline);
                if (user.getSocket() != null)
                    user.getSocket().stopThread();
            }
        for (Observer observer : this.observers)
            observer.updateUserList("setUserOffline", new String[] { username });
    }

    public void setUsersOffline(ArrayList<User> users) {
        for (User user : users)
            setUserOffline(user.getUsername());
    }

    public void setUsername(String ipAddress, String newUsername) {
        for (User user : this.arrayUsers)
            if (user.getIpAddress().equals(ipAddress)) {
                for (Observer observer : this.observers)
                    observer.updateUserList("setUsername", new String[] { user.getUsername(), newUsername });
                user.setUsername(newUsername);
                user.setChange(true);
                user.modUser = new ModUser(user.modUser.mod, user.modUser.userID, newUsername, user.modUser.online);
            }

    }

    public User getUserByUsername(String username) {
        for (User user : this.arrayUsers)
            if (user.getUsername().equals(username))
                return user;
        return null;
    }

    public User getActiveUserByUsername(String username) {
        for (User user : arrayUsers) {
            if (user.getUsername().equals(username) && user.isActive()) {
                return user;
            }
        }
        return null;
    }

    public ArrayList<User> getActiveUsers() {
        ArrayList<User> lst = new ArrayList<User>();
        for (User user : this.arrayUsers)
            if (user.isActive())
                lst.add(user);
        lst.add(this.me);
        return lst;
    }

    public ArrayList<User> getAllUsers() {
        ArrayList<User> lst = new ArrayList<User>(this.arrayUsers);
        lst.add(this.me);
        return lst;
    }

    public ArrayList<User> getActiveUsersExceptMe() {
        ArrayList<User> lst = new ArrayList<User>();
        for (User user : this.arrayUsers)
            if (user.isActive() && !user.equals(this.me))
                lst.add(user);
        return lst;
    }

    public void decrementTTL() {
        for (User user : this.arrayUsers) {
            user.decrementTTL();
            if (user.getTimeToLive() == 0) {
                this.setUserOffline(user.getUsername());
            }
        }
    }

    public void setMe(User me) {
        this.me = me;
    }

    public User getMe() {
        return me;
    }

    public void deleteUsers() {
        this.arrayUsers.clear();
    }

    public User getUserByIpAddress(String ipAddress) {
        for (User user : this.arrayUsers)
            if (user.getIpAddress().equals(ipAddress))
                return user;
        return null;
    }

    public void updateUDPServer(String type, String[] args) {
        switch (type) {
            case "NewUser":
                this.addUser(new User(args[0], args[1], true, true, args[2]));
                break;
            case "SetUserOffline":
                this.setUserOffline(args[0]);
                break;
            case "UpdateUsername":
                this.setUsername(args[1], args[0]);
                break;
            case "TimeToLive":
                User user = this.getUserByIpAddress(args[0]);
                if (user != null) {
                    if (user.isActive())
                        user.setTimeToLive();
                    else {
                        System.err.println("User is offline");
                        user.setActive(true);
                        user.modUser = new ModUser(user.modUser.mod, user.modUser.userID, user.modUser.username,
                                ModUser.Status.Online);
                        for (Observer observer : this.observers)
                            observer.updateUserList("addUser", new String[] { "DA", user.getUsername() });
                    }
                } // else LOGGER.warn("User not found");
                break;
        }
    }

    public void connectTCP(String ipAddr, TCPClient socket) {
        User user = this.getUserByIpAddress(ipAddr);
        if (user != null) {
            user.setSocket(socket);
            for (Observer observer : this.observers)
                observer.updateUserList("connectTCP", new String[] { user.getUsername() });
        } else
            System.err.println("User not found: " + ipAddr);
    }

    public void connectResponseTCP(String ipAddr, boolean response) {
        User user = this.getUserByIpAddress(ipAddr);
        if (user == null)
            return;
        if (!response) {
            user.setSocket(null);
            for (Observer observer : this.observers)
                observer.updateUserList("RefuseSession", new String[] { user.getUsername() });
        } else
            for (Observer observer : this.observers)
                observer.updateUserList("AcceptSession", new String[] { user.getUsername() });
    }

    public void messageTCP(String ipAddr, String message) {
        User user = this.getUserByIpAddress(ipAddr);
        if (user == null)
            return;
        user.addMessage(new ModMessage(Clavardons4JS.MOD_INFO, me.modUser.userID, me.modUser.username, ipAddr, message,
                Clavardons4JS.getTime(), false));
        for (Observer observer : this.observers)
            observer.updateUserList("messageTCP", new String[] { user.getUsername(), message });
    }

    public void quitTCP(String ipAddr) {
        User user = this.getUserByIpAddress(ipAddr);
        if (user != null)
            user.setSocket(null);
        for (Observer observer : this.observers)
            observer.updateUserList("quitTCP", new String[] { user.getUsername() });
    }
}
