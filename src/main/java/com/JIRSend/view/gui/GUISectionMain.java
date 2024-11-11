package com.JIRSend.view.gui;

import javax.swing.*;
import java.awt.*;

public class GUISectionMain extends GUISection {

    private GuiPanelMainChatSystem mainPanel;

    public GUISectionMain(MainGUI window, Frame frame) {
        super(window, frame, "JIRSend main");
        this.mainPanel = new GuiPanelMainChatSystem(window.controller);
    }

    public JPanel createPanel() {
        return mainPanel.getPanel();
    }

    protected void createActions() {
    }

}
