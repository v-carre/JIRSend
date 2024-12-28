package com.JIRSend.mods;

import java.net.SocketException;
import java.util.HashMap;
import java.util.List;

import com.JIRSend.controller.MainController;
import com.JIRSend.controller.Pipe;
import com.JIRSend.view.cli.Log;
import com.JIRSend.view.gui.ErrorPopup;

public class ModController {
    private String controllerName;
    private final MainController mainController;

    // Mods
    HashMap<String, JIRSendMod> mods;

    // Pipes
    public static Pipe<ModUser> contactChange = new Pipe<>("MOD: contact change");
    public static Pipe<ModMessage> messageReceived = new Pipe<>("MOD: Message received");

    public ModController(String name, MainController mainController) throws SocketException {
        this.controllerName = name;
        this.mainController = mainController;
        this.mods = new HashMap<>();
        ModLoader modLoader = new ModLoader();
        List<JIRSendMod> modList = modLoader.loadMods();
        for (JIRSendMod jirSendMod : modList) {
            String modID = jirSendMod.getModInformation().id;
            if (this.mods.containsKey(modID)) {
                signalError("2 mods are using the same ID: " + this.mods.get(modID)
                        + " and " + jirSendMod);
                mainController.stoppingApp(15);
            }
            mods.put(modID, jirSendMod);
        }
        for (JIRSendMod mod : mods.values()) {
            mod.initialize(this);
        }
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
        for (JIRSendMod mod : mods.values()) {
            mod.stop();
        }
    }

    /// Setters
    public void changeUsername(String username) {
        for (JIRSendMod mod : mods.values()) {
            mod.changeUsername(username);
        }
    }

    public boolean isUsernameAvailable(String username) {
        for (JIRSendMod mod : mods.values()) {
            if (!mod.isUsernameAvailable(username))
                return false;
        }
        return true;
    }

    /// Getters
    public String getUsername() {
        return this.mainController.getUsername();
    }

    public void signalError(String error) {
        Log.e("MOD ERROR: " + error);
        if (mainController.isUsingGUI()) {
            ErrorPopup.show("MOD ERROR", error);
        }
    }
}
