package com.gestionProjet.ui;

import java.awt.*;
import javax.swing.*;

public abstract class GUISection {
    private String sectionName;
    protected MainWindow window;
    protected Frame frame;

    protected GUISection(MainWindow window, Frame frame, String name) {
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
