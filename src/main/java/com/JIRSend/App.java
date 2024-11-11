package com.JIRSend;

import com.JIRSend.controller.MainController;
import com.JIRSend.view.cli.CliTools;
import com.JIRSend.view.cli.Log;

public class App {
    public static void main(String[] args) {
        boolean cliFlag = false;
        for (String arg : args) {
            if (arg.equals("--cli")) {
                cliFlag = true;
            }
        }

        CliTools.clearConsole();
        CliTools.printLogo();
        CliTools.printTitle(true);
        Log.setVerbose(true, Log.ALL);
        Log.l("Starting Client...", Log.LOG);

        MainController controller = new MainController(!cliFlag);
        controller.startUI();
    };
}
