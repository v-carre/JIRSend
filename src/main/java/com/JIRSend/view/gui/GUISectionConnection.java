package com.JIRSend.view.gui;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.JIRSend.view.cli.Log;

import java.awt.*;

public class GUISectionConnection extends GUISection {
    private JTextField username;
    private JLabel JIRSendLogo;
    private JButton connect;

    private Action submitAction;
    // our icons for the actions
    // ImageIcon cutIcon, copyIcon, pasteIcon;

    public GUISectionConnection(MainGUI window, Frame frame) {
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
            super(new BorderLayout());
            setBorder(new EmptyBorder(10, 40, 10, 40));
            setBackground(GuiPanelMainChatSystem.bodyBGColor);
            setForeground(GuiPanelMainChatSystem.whitestColor);

            JIRSendLogo = new JLabel(
                    new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/jirsend_logo.png"))
                            .getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
            JIRSendLogo.setBorder(BorderFactory.createEmptyBorder(50, 10, 10, 10));

            JLabel usernameLabel = new JLabel("Username");
            Font boldFont = GuiTools.getFont("Monospaced", Font.BOLD, 16, usernameLabel.getFont());
            usernameLabel.setFont(boldFont);
            usernameLabel.setForeground(GuiPanelMainChatSystem.almostWhiteColor);
            JLabel errorMessage = new JLabel(
                    "<html><body><p style=\"width:180px; background:red; padding:10px; border-radius:10px; text-align:center;\">"
                            + window.lastError + "</p></body></html>",
                    SwingConstants.CENTER);
            // errorMessage.setEditable(false);
            // errorMessage.setBorder(new GuiRoundedBorder(0));
            errorMessage.setOpaque(false);
            // errorMessage.setAlignmentX(0);
            // errorMessage.setUI(MultilineLabelUI.labelUI);
            errorMessage.setForeground(new Color(255, 255, 255));
            // errorMessage.setBackground();
            // errorMessage.setBorder(new GuiRoundedBorder(20));
            username = new RoundJTextField(17);
            username.setHorizontalAlignment(SwingConstants.CENTER);
            username.setBorder(new GuiRoundedBorder(10));
            Font normalFont = GuiTools.getFont("Monospaced", Font.PLAIN, 16, username.getFont());
            username.setFont(normalFont);
            username.setCaretColor(GuiPanelMainChatSystem.almostWhiteColor);
            username.setForeground(GuiPanelMainChatSystem.almostWhiteColor);
            username.setBackground(GuiPanelMainChatSystem.chatBGColor);
            // ImageIcon image = new ImageIcon("");+
            connect = new JButton(submitAction);
            // connect.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
            connect.setBorder(new GuiRoundedBorder(10, 0, 52, 0, 52));
            connect.setBackground(GuiPanelMainChatSystem.headerFooterBGColor);
            connect.setForeground(GuiPanelMainChatSystem.almostWhiteColor);
            connect.setMargin(new Insets(10, 10, 10, 10));
            connect.setFont(boldFont);
            // connect.setSize(200,200);
            final JSButtonUI ui = new JSButtonUI();
            ui.setPressedColor(connect.getBackground().darker());
            connect.setUI(ui);
            connect.setCursor(new Cursor(Cursor.HAND_CURSOR));
            connect.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel panel0 = new JPanel();
            panel0.setOpaque(false);
            // panel0.setOpaque(false);
            panel0.add(errorMessage);
            // panel0.set

            JPanel panel1 = new JPanel();
            JPanel innerPanel1 = new JPanel();
            innerPanel1.setLayout(new GridLayout(2, 0, 5, 5));
            innerPanel1.add(usernameLabel);
            innerPanel1.add(username);
            innerPanel1.setOpaque(false);
            innerPanel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            panel1.add(innerPanel1);
            panel1.setOpaque(false);

            JPanel panel2 = new JPanel();
            panel2.setOpaque(false);

            setLayout(new GridLayout(3, 0));

            add(JIRSendLogo);// , BorderLayout.CENTER);
            // panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
            panel2.add(connect);
            if (window.lastError != "") {
                // add(errorMessage);
                panel2.add(errorMessage);// , BorderLayout.NORTH);
                errorMessage.setVisible(true);
                window.lastError = "";
            } else {
                // errorMessage.setVisible(false);
                // setLayout(new GridLayout(3, 1));
            }
            // panel2.setHorizontalAlignment(SwingConstants.CENTER);
            add(panel1);// , BorderLayout.SOUTH);
            add(panel2);// , BorderLayout.SOUTH);
        }
    }

    private class SubmitConnectionAction extends AbstractAction {
        public SubmitConnectionAction() {
            super("Connection");
        }

        public void actionPerformed(ActionEvent action) {
            String usernameAsked = username.getText();
            if (usernameAsked.length() < 2) {
                String usernameError = "Username should have at least 2 characters.";
                ErrorPopup.show("Connection impossible", usernameError);
                window.lastError = usernameError;
                window.refreshSection();
                return;
            }

            Log.l("Connecting as '" + usernameAsked + "'", Log.LOG);

            if (window.controller.changeUsername(usernameAsked)) {
                // window.username = usernameAsked;
                window.switchToNextSection();
            } else {
                String usernameError = "'" + usernameAsked + "' is not available";
                ErrorPopup.show("Connection impossible", usernameError);
                window.lastError = usernameError;
                window.refreshSection();
            }
        }
    }
}
