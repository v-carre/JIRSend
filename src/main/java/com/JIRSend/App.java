package com.JIRSend;

import java.net.SocketException;

import com.JIRSend.controller.MainController;
import com.JIRSend.view.cli.CliTools;
import com.JIRSend.view.cli.Log;
import com.JIRSend.view.gui.ErrorPopup;

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
