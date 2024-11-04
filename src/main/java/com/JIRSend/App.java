package com.JIRSend;

import com.JIRSend.ui.Log;
import com.JIRSend.ui.MainWindow;

public class App {
    public static void main(String[] args) {
        Log.setVerbose(true, Log.ALL);
        Log.l("Starting Client...", Log.LOG);

        new MainWindow().open();
    };
}
