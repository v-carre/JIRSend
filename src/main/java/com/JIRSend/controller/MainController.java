
package com.JIRSend.controller;

import com.JIRSend.network.Net;
import com.JIRSend.ui.MainWindow;
import com.JIRSend.users.BaseUser;
import com.JIRSend.users.User;

public class MainController {
    private String controllerName;

    // View objects
    private MainWindow view;
    
    // Model objects
    protected BaseUser user;
    protected Net net;

    // Pipes
    public static Pipe<String> localUsernameChange = new Pipe<>("localUsernameChanged");

    public MainController(String name) {
        this.controllerName = name;
        this.view = new MainWindow(this);
        this.net = new Net(this);
        this.user = new User(this);
    }

    public MainController() {
        this("JIRSend Main");
    }

    public void startWindow() {
        this.view.open();
    }

    public String getName() {
        return controllerName;
    }

    //////// VIEW
    /// Setters
    public boolean changeUsername(String username) {
        if (this.net.usernameAvailable(username))
        {
            this.user.setUsername(username);
            return true;
        }
        return false; 
    }

    /// Getters
    public String getUsername() {
        return this.user.getUsername();
    }
}
