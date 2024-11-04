package com.JIRSend.users;

public abstract class BaseUser {
    protected enum userType {
        User, Admin
    }

    protected userType type;
    protected String username;
    protected int id;

    protected BaseUser(String username, userType type) {
        this.username = username;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public userType getType() {
        return this.type;
    }
}
