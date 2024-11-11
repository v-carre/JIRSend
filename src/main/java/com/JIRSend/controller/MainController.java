package com.JIRSend.controller;

import com.JIRSend.model.network.Net;
import com.JIRSend.model.user.BaseUser;
import com.JIRSend.model.user.User;
import com.JIRSend.view.MainAbstractView;
import com.JIRSend.view.cli.MainCLI;
import com.JIRSend.view.gui.MainGUI;

public class MainController {
    private String controllerName;

    // View objects
    private MainAbstractView view;
    
    // Model objects
    protected BaseUser user;
    protected Net net;

    // Pipes
    public static Pipe<String> localUsernameChange = new Pipe<>("localUsernameChanged");

    public MainController(String name, boolean usingGUI) {
        this.controllerName = name;
        if (usingGUI)
            this.view = new MainGUI(this);
        else
            this.view = new MainCLI(this);
        this.net = new Net(this);
        this.user = new User(this);
    }

    public MainController(boolean usingGUI) {
        this("JIRSend Main", usingGUI);
    }

    public void startUI() {
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
