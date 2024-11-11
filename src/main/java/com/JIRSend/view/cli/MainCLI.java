package com.JIRSend.view.cli;

import com.JIRSend.controller.MainController;
import com.JIRSend.view.MainAbstractView;

public class MainCLI extends MainAbstractView {
    
    protected MainController controller;
    private CliThread thread;

    public MainCLI(MainController controller) {
        this.controller = controller;
        this.thread = new CliThread();
    }

    @Override
    public void open() {
        Log.l("Starting CLI thread");
        this.thread.start();
    }

    private class CliThread extends Thread {
        @Override
        public void run() {
            // System.out.print("");
        }
    }
}
