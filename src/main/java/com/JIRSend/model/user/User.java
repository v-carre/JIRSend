package com.JIRSend.model.user;

import com.JIRSend.controller.MainController;

public class User extends BaseUser {

    public User(MainController controller, String username) {
        super(controller, username, userType.User);
    }

    public User(MainController controller) {
        this(controller, null);
    }
}
