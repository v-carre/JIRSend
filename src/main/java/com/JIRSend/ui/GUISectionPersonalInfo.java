package com.JIRSend.ui;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class GUISectionPersonalInfo extends GUISection {
    // private JComboBox<String> userType;
    // private JTextField firstName, lastName;

    @SuppressWarnings("unused")
    private Action noAction;
    // our icons for the actions
    // ImageIcon cutIcon, copyIcon, pasteIcon;

    public GUISectionPersonalInfo(MainWindow window, Frame frame) {
        super(window, frame, "Personnal Information");
    }

    public JPanel createPanel() {
        return new PersonalPanel();
    }

    protected void createActions() {
        // subIcon = new
        // ImageIcon(Connection.class.getResource("assets/connect-button.jpg"));
        noAction = new noAction();
    }

    private class PersonalPanel extends JPanel {
        public PersonalPanel() {
            // super();
            super(new BorderLayout());

            // setLayout(new GridLayout(3, 1));
            add(new JLabel("Welcome " + window.username));
            // add(new JLabel("You are a " + window.user.getType()));
            // add(new JLabel("Nom:" + window.user.getNom()));
            // add(new JLabel("Prenom:" + window.user.getPrenom()));
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
