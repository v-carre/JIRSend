package com.JIRSend;

import com.JIRSend.cli.CliTools;
import com.JIRSend.cli.Log;
import com.JIRSend.controller.MainController;

public class App {
    public static void main(String[] args) {
        CliTools.clearConsole();
        CliTools.printLogo();
        CliTools.printTitle(true);
        Log.setVerbose(true, Log.ALL);
        Log.l("Starting Client...", Log.LOG);

        MainController controller = new MainController();
        controller.startWindow();
    };
}
