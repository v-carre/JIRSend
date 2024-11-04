package com.JIRSend.ui;

import java.awt.event.*;
import javax.swing.*;

import com.JIRSend.users.*;

import java.awt.*;

public class GUISectionConnection extends GUISection {
    private JTextField username;

    private Action submitAction;
    // our icons for the actions
    // ImageIcon cutIcon, copyIcon, pasteIcon;

    public GUISectionConnection(MainWindow window, Frame frame) {
        super(window, frame, "Connection");
    }

    public JPanel createPanel() {
        return new ConnectionPanel();
    }

    protected void createActions() {
        // subIcon = new
        // ImageIcon(Connection.class.getResource("assets/connect-button.jpg"));
        submitAction = new SubmitConnectionAction();
    }

    private class ConnectionPanel extends JPanel {
        public ConnectionPanel() {
            // super();
            super(new BorderLayout());
            JLabel usernameLabel = new JLabel("Username");
            JLabel notAvailableLabel = new JLabel(window.lastError);
            notAvailableLabel.setOpaque(true);
            notAvailableLabel.setForeground(new Color(255, 255, 255));
            notAvailableLabel.setBackground(new Color(255, 0, 0));
            username = new JTextField(20);
            // ImageIcon image = new ImageIcon("");+
            JButton connect = new JButton(submitAction);
            // connect.setSize(200,200);

            JPanel panel0 = new JPanel();
            panel0.add(notAvailableLabel);

            JPanel panel1 = new JPanel();
            panel1.add(usernameLabel);
            panel1.add(username);

            JPanel panel2 = new JPanel();
            panel2.add(connect);

            setLayout(new GridLayout(3, 1));
            if (window.lastError != "") {
                add(panel0);
                window.lastError = "";
            }
            add(panel1);
            add(panel2);
        }
    }

    private class SubmitConnectionAction extends AbstractAction {
        public SubmitConnectionAction() {
            super("Connexion");
        }

        public void actionPerformed(ActionEvent action) {
            String usernameAsked = username.getText();
            Log.l("Connection de '" + usernameAsked + "'");

            if (window.net.usernameAvailable(usernameAsked)) {
                window.username = usernameAsked;
                window.switchToNextSection();
            } else {
                String usernameError = "'" + usernameAsked + "' is not available";
                ErrorPopup.show("Connexion impossible", usernameError);
                window.lastError = usernameError;
                window.refreshSection();
            }
        }
    }
}
