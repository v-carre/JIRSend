
package com.JIRSend.controler;

import com.JIRSend.network.Net;
import com.JIRSend.ui.MainWindow;
import com.JIRSend.users.BaseUser;

public class MainController {
    private String controlerName;

    // View objects
    private MainWindow view;
    
    // Model objects
    protected String username;
    protected BaseUser user;
    protected Net net;

    public MainController(String name) {
        this.controlerName = name;
        this.view = new MainWindow(this);
        this.net = new Net(this);
    }

    public MainController() {
        this("JIRSend Main");
    }

    public void startWindow() {
        this.view.open();
    }

    public String getName() {
        return controlerName;
    }

    //////// VIEW
    /// Setters
    public boolean checkUsername(String username) {
        return this.net.isUsernameValid(username);
    }
    /// Getters
    public String getUsername() {
        return this.username;
    }
}
