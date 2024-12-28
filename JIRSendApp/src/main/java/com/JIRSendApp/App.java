package com.JIRSendApp;

import java.net.SocketException;

import com.JIRSendApp.controller.MainController;
import com.JIRSendApp.view.cli.CliTools;
import com.JIRSendApp.view.cli.Log;
import com.JIRSendApp.view.gui.ErrorPopup;

public class App {
    public static void main(String[] args) {
        boolean cliFlag = false;
        boolean verboseFlag = false;
        for (String arg : args) {
            if (arg.equals("--cli") || arg.equals("--no-gui")) {
                cliFlag = true;
            }
            if (arg.equals("--debug") || arg.equals("--verbose")) {
                verboseFlag = true;
            }
        }

        CliTools.clearConsole();
        CliTools.printLogo();
        CliTools.printTitle(true);
        Log.setVerbose(verboseFlag, Log.ALL);
        Log.l("Starting Client...", Log.LOG);

        try {
            new MainController(!cliFlag);
        } catch (SocketException e) {
            Log.e("Error while starting app: Could not create socket");
            if (!cliFlag)
                ErrorPopup.show("JIRSend could not start", "Could not create socket");
            e.printStackTrace();
            System.exit(1);
        }
    };
}
