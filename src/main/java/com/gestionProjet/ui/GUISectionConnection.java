package com.gestionProjet.ui;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;

public class GUISectionConnection extends GUISection {
    private JComboBox<String> userType;
    private JTextField firstName, lastName;

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

            JLabel choix = new JLabel("Type d'utilisateur ");
            String[] possibilities = { "User", "Volontaire", "El Decidor" };
            userType = new JComboBox<>(possibilities);
            JLabel lastNameLabel = new JLabel("Nom");
            lastName = new JTextField(20);
            JLabel firstNameLabel = new JLabel("Prenom");
            firstName = new JTextField(20);
            // ImageIcon image = new ImageIcon("");+
            JButton connect = new JButton(submitAction);
            // connect.setSize(200,200);

            JPanel panel1 = new JPanel();
            panel1.add(choix);
            panel1.add(userType);

            JPanel panel2 = new JPanel();
            panel2.add(lastNameLabel);
            panel2.add(lastName);

            JPanel panel3 = new JPanel();
            panel3.add(firstNameLabel);
            panel3.add(firstName);

            JPanel panel4 = new JPanel();
            panel4.add(connect);

            setLayout(new GridLayout(4, 1));
            add(panel1);
            add(panel2);
            add(panel3);
            add(panel4);
        }
    }

    private class SubmitConnectionAction extends AbstractAction {
        public SubmitConnectionAction() {
            super("Connexion");
        }

        public void actionPerformed(ActionEvent action) {
            if (userType.getSelectedItem() == null || lastName.getText().isEmpty() || firstName.getText().isEmpty()) {
                Log.l("Empty field !");
                return;
            }
            Log.l("Connection de " + userType.getSelectedItem() + " " + lastName.getText() + " " + firstName.getText());

            window.setUser(firstName.getText(), lastName.getText(), userType.getSelectedItem().toString());

            window.switchToNextSection();
        }
    }
}
