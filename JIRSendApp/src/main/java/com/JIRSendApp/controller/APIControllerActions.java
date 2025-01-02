package com.JIRSendApp.controller;

import java.util.ArrayList;

import com.JIRSendAPI.JIRSendMod.JIRSendModInformation;
import com.JIRSendAPI.ModController.ModControllerActions;
import com.JIRSendApp.model.user.UserEntry;
import com.JIRSendApp.view.cli.Log;
import com.JIRSendApp.view.gui.ErrorPopup;

public class APIControllerActions implements ModControllerActions {
    private final MainController mainController;

    public APIControllerActions(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * To signal error with popup (if GUI)
     * 
     * @param error
     */
    public void signalError(String error) {
        Log.e("MOD ERROR: " + error);
        System.err.println("MOD ERROR: " + error);
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
        System.err.println("MOD ERROR: " + error);
        if (mainController.isUsingGUI()) {
            ErrorPopup.show("MOD ERROR", error);
        }
        int ex = 16 + Math.min(15, Math.max(exitStatus, 0));
        this.mainController.stoppingApp(ex);
    }

    public String getUsername() {
        return this.mainController.getUsername();
    }

    @Override
    public boolean isUsernameAvailable(String username, JIRSendModInformation info) {
        return this.mainController.isUsernameAvailableMod(username, info.id) && !username.equals(this.mainController.getUsername());
    }

    @Override
    public ArrayList<String> getConnectedUsernames() {
        ArrayList<String> rtn = new ArrayList<>();
        ArrayList<UserEntry> uelist = this.mainController.getContacts();
        for (UserEntry ue : uelist) {
            if (ue.online())
                rtn.add(ue.username);
        }
        return rtn;
    }
}
