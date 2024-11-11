package com.JIRSend.view.cli;

import com.JIRSend.controller.MainController;
import com.JIRSend.view.MainAbstractView;

public class MainCLI extends MainAbstractView {
    
    protected MainController controller;

    public MainCLI(MainController controller) {
        this.controller = controller;
    }

    @Override
    public void open() {
        Log.l("Starting CLI thread");
    }
}
