package com.JIRSend.ui;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class GUISectionMain extends GUISection {
    // private JComboBox<String> userType;
    // private JTextField firstName, lastName;

    @SuppressWarnings("unused")
    private Action noAction;
    // our icons for the actions
    // ImageIcon cutIcon, copyIcon, pasteIcon;

    private GuiPanelMainChatSystem mainPanel;

    public GUISectionMain(MainWindow window, Frame frame) {
        super(window, frame, "JIRSend main");
        this.mainPanel = new GuiPanelMainChatSystem(window.controller);
    }

    public JPanel createPanel() {
        return mainPanel.getPanel(); //new PersonalPanel();
    }

    protected void createActions() {
        // subIcon = new
        // ImageIcon(Connection.class.getResource("assets/connect-button.jpg"));
        noAction = new noAction();
    }

    // private class PersonalPanel extends JPanel {
    //     public PersonalPanel() {
    //         // super();
    //         super(new BorderLayout());

    //         // setLayout(new GridLayout(3, 1));
    //         add(new JLabel("Welcome " + window.controller.getUsername()));
    //         // add(new JLabel("You are a " + window.user.getType()));
    //         // add(new JLabel("Nom:" + window.user.getNom()));
    //         // add(new JLabel("Prenom:" + window.user.getPrenom()));
    //     }
    // }

    private class noAction extends AbstractAction {
        public noAction() {
            super("No action defined");
        }

        public void actionPerformed(ActionEvent action) {
            
        }
    }

}
