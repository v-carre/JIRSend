package com.JIRSendApp.view.gui;

import java.awt.*;
import javax.swing.*;

public abstract class GUISection {
    private String sectionName;
    protected MainGUI window;
    protected Frame frame;

    protected GUISection(MainGUI window, Frame frame, String name) {
        this.window = window;
        this.frame = frame;
        this.sectionName = name;
        createActions();
    }

    public String getSectionName() {
        return sectionName;
    }

    protected abstract void createActions();
    public abstract JPanel createPanel();
}
