package com.JIRSendApp.view.gui;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class GUISectionPersonalInfo extends GUISection {

    @SuppressWarnings("unused")
    private Action noAction;

    public GUISectionPersonalInfo(MainGUI window, Frame frame) {
        super(window, frame, "Personnal Information");
    }

    public JPanel createPanel() {
        return new PersonalPanel();
    }

    protected void createActions() {
        noAction = new noAction();
    }

    private class PersonalPanel extends JPanel {
        public PersonalPanel() {
            super(new BorderLayout());

            add(new JLabel("Welcome " + window.controller.getUsername()));
        }
    }

    private class noAction extends AbstractAction {
        public noAction() {
            super("No action defined");
        }

        public void actionPerformed(ActionEvent action) {
            
        }
    }

}
