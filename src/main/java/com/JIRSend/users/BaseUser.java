package com.JIRSend.users;

import com.JIRSend.controller.MainController;

public abstract class BaseUser {
    protected enum userType {
        User, Admin
    }

    protected MainController controller;
    protected userType type;
    protected String username;
    protected int id;

    protected BaseUser(MainController controler, String username, userType type) {
        this.controller = controler;
        this.username = username;
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public userType getType() {
        return this.type;
    }
}
