package com.JIRSendAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.JIRSend.controller.MainController;
import com.JIRSend.controller.Pipe;
import com.JIRSend.view.cli.Log;
import com.JIRSend.view.gui.ErrorPopup;
import com.JIRSendAPI.JIRSendMod.JIRSendModInformation;

public class ModController {
    private String controllerName;
    private final MainController mainController;

    // Mods
    HashMap<String, JIRSendMod> mods;

    // Pipes
    public static Pipe<ModUser> contactChange = new Pipe<>("MOD: contact change");
    public static Pipe<ModMessage> messageReceived = new Pipe<>("MOD: Message received");

    public ModController(String name, MainController mainController) {
        this.controllerName = name;
        this.mainController = mainController;
        this.mods = new HashMap<>();
        ModLoader modLoader = new ModLoader();
        List<JIRSendMod> modList = modLoader.loadMods();
        System.out.println("Loading mods... (" + modList.size() + " found)");
        for (JIRSendMod jirSendMod : modList) {
            String modID = jirSendMod.getModInformation().id;
            if (this.mods.containsKey(modID))
                signalErrorAndStop("2 mods are using the same ID: " + this.mods.get(modID)
                        + " and " + jirSendMod, 0);
            mods.put(modID, jirSendMod);
        }
        for (JIRSendMod mod : mods.values()) {
            mod.initialize(this);
            System.out.println(mod.getModInformation() + " loaded.");
        }
    }

    public ModController(MainController mainController) {
        this("JIRSend Mod Controller", mainController);
    }

    public String getName() {
        return controllerName;
    }

    public ArrayList<JIRSendModInformation> getModsInformation(String username) {
        ArrayList<JIRSendModInformation> infos = new ArrayList<>();
        for (JIRSendMod mod : mods.values()) {
            infos.add(mod.getModInformation());
        }
        return infos;
    }

    /**
     * Will stop the mods
     */
    public void stop() {
        for (JIRSendMod mod : mods.values()) {
            mod.stop();
        }
    }

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

    public String getUsername() {
        return this.mainController.getUsername();
    }

    /**
     * To signal error with popup (if GUI)
     * 
     * @param error
     */
    public void signalError(String error) {
        Log.e("MOD ERROR: " + error);
        if (mainController.isUsingGUI()) {
            ErrorPopup.show("MOD ERROR", error);
        }
    }

    /**
     * Signals error with popup (if GUI) and STOPS the app
     * 
     * @warning use it carefully!
     * @param error
     * @param exitStatus between 0 and 15
     */
    public void signalErrorAndStop(String error, int exitStatus) {
        Log.e("MOD ERROR: " + error);
        if (mainController.isUsingGUI()) {
            ErrorPopup.show("MOD ERROR", error);
        }
        int ex = 16 + Math.min(15, Math.max(exitStatus, 0));
        this.mainController.stoppingApp(ex);
    }
}
