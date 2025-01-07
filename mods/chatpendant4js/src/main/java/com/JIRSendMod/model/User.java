package com.JIRSendMod.model;

import java.net.InetAddress;
import java.net.Socket;
import java.util.regex.Pattern;

/**
 * User of the system
 *
 * Identified by their IP address. Nickname is modifiable.
 */
public class User {

    private InetAddress ip;
    private String nickname;
    private String quote; // An inspiring quote that user can provide
    private UserStatus status; // Information about user availability
    private Socket socketClient;

    public void setSocketClient(Socket socketClient) {
        this.socketClient = socketClient;
    }

    /**
     * Instantiate an existing User
     *
     * @param ip IP created when user was first created
     * @param nickname Unique nickname
     * @param quote An inspiring quote that user can provide
     * @param status Information about user availability
     */
    public User(InetAddress ip, String nickname, String quote, UserStatus status) {
        this.ip = ip;
        this.nickname = nickname;
        this.quote = quote;
        this.status = status;
    }

    public InetAddress getIp() {
        return ip;
    }

    public String getNickname() {
        return nickname;
    }

    /**
     * Update nickname
     *
     * Does not update in db, does not do any checks, does not notify network
     * @param newNickname
     */
    public void setNickname(String newNickname) {
        this.nickname = newNickname;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final User user = (User) obj;
        return this.nickname.equals(user.getNickname());
    }

    // Username RegEx
    private static Pattern nicknamePattern = Pattern.compile("^[a-z]{3,12}$");

    /**
     * Evaluate whether a nickname can be used. Does NOT check whether it is already used by another user
     *
     * @param nickname The nickname to be evaluated
     * @return Whether the nickname is acceptable
     */
    public static boolean isValidNickname(String nickname) {
        return nickname != null && nicknamePattern.matcher(nickname).find();
    }

    public String getQuote() {
        return quote;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String toString() {
        return (
            "-------------------------------" +
            "\n - " +
            ((this.getIp() == null)
                    ? "No IP specified "
                    : this.getIp().getHostAddress()) +
            "\n - " +
            this.getNickname() +
            "\n - " +
            this.getQuote() +
            "\n - " +
            this.getStatus().getDescription() +
            "\n - " +
            "-------------------------------\n"
        );
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }
}
