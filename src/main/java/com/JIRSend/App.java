package com.JIRSend;

import com.JIRSend.controler.MainController;
import com.JIRSend.ui.Log;

public class App {
    public static void main(String[] args) {
        Log.setVerbose(true, Log.ALL);
        Log.l("Starting Client...", Log.LOG);

        MainController controler = new MainController();
        controler.startWindow();
    };
}
