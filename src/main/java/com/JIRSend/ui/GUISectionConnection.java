package com.JIRSend.ui;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;

public class GUISectionConnection extends GUISection {
    private JTextField username;
    private JLabel JIRSendLogo;
    private JButton connect;

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
                            + window.lastError + "</p></body></html>");
            // errorMessage.setEditable(false);
            errorMessage.setOpaque(false);
            // errorMessage.setUI(MultilineLabelUI.labelUI);
            errorMessage.setForeground(new Color(255, 255, 255));
            // errorMessage.setBackground();
            // errorMessage.setBorder(new GuiRoundedBorder(20));
            username = new RoundJTextField(17);
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
            panel2.add(connect);
            panel2.setOpaque(false);

            setLayout(new GridLayout(4, 1));
            add(JIRSendLogo);
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
