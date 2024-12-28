package com.JIRSend.mods;

import java.net.SocketException;

import com.JIRSend.controller.MainController;
import com.JIRSend.controller.Pipe;
import com.JIRSend.model.db.LocalDatabase;
import com.JIRSend.model.network.Net;
import com.JIRSend.model.user.BaseUser;

public class ModController {
    private String controllerName;
    private final MainController mainController;

    // Model objects
    protected BaseUser user;
    protected Net net;
    protected LocalDatabase db;

    // Pipes
    public static Pipe<ModUser> contactChange = new Pipe<>("MOD: contact change");
    public static Pipe<ModMessage> messageReceived = new Pipe<>("MOD: Message received");

    public ModController(String name, MainController mainController) throws SocketException {
        this.controllerName = name;
        this.mainController = mainController;
    }

    public ModController(MainController mainController) throws SocketException {
        this("JIRSend Mod Controller", mainController);
    }

    public String getName() {
        return controllerName;
    }
    /**
     * Will stop the mods
     */
    public void stop() {
        // TODO: unhandled
    }

    /// Setters
    public void changeUsername(String username) {
        // TODO: implementation
    }

    public boolean isUsernameAvailable(String username) {
        // TODO: implementation
        return false;
    } 

    /// Getters
    public String getUsername() {
        return this.mainController.getUsername();
    }
}
