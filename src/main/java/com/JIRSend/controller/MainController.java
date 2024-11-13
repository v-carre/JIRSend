package com.JIRSend.controller;

import java.util.ArrayList;

import com.JIRSend.model.Message;
import com.JIRSend.model.network.Net;
import com.JIRSend.model.user.BaseUser;
import com.JIRSend.model.user.User;
import com.JIRSend.model.user.UserEntry;
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
    public static Pipe<String> lostContact = new Pipe<>("Lost Contact");
    public static Pipe<String> localUsernameChange = new Pipe<>("Local Username Changed");
    public static Pipe<String> contactsChange = new Pipe<>("Contacts Changed");
    public static Pipe<Message> sendMessage = new Pipe<>("Sending Message");
    public static Pipe<Message> messageReceived = new Pipe<>("Message received");

    public MainController(String name, boolean usingGUI) {
        this.controllerName = name;
        if (usingGUI)
            this.view = new MainGUI(this);
        else
            this.view = new MainCLI(this);
        this.user = new User(this);
        // start UI when Net is setup
        this.net = new Net(this, () -> {
            startUI();
        });
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
    public String changeUsername(String username) {
        String res = this.net.usernameAvailable(username);
        if (res.equals(Net.okString)) {
            this.user.setUsername(username);
            return Net.okString;
        }
        return res;
    }

    /// Getters
    public String getUsername() {
        return this.user.getUsername();
    }

    public ArrayList<UserEntry> getContacts() {
        return net.getUserEntries();
    }

    public int getNumberConnected() {
        int connected = 0;
        for (UserEntry ue : net.getUserEntries()) {
            if (ue.online)
                connected++;
        }
        return connected;
    }

    public ArrayList<String> getConnectedUsernames() {
        ArrayList<String> connected = new ArrayList<>();
        for (UserEntry ue : net.getUserEntries()) {
            if (ue.online)
                connected.add(ue.username);
        }
        return connected;
    }
}
