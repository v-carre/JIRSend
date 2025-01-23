package com.JIRSendApp.view.gui;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.JIRSendApp.view.cli.Log;

import java.awt.*;

public class GUISectionConnection extends GUISection {
    private JTextField username;
    private JLabel JIRSendLogo;
    private JButton connect, mods;

    private Action submitAction, getmodsAction;

    public GUISectionConnection(MainGUI window, Frame frame) {
        super(window, frame, "Connection");
    }

    public JPanel createPanel() {
        JPanel panel = new ConnectionPanel();
        if (mods != null && window.controller.modc != null)
            mods.setText(window.controller.modc.getModsInformation().size() > 0
                    ? "Mods (" + window.controller.modc.getModsInformation().size() + ")"
                    : "No mods loaded");
        return panel;
    }

    protected void createActions() {
        // subIcon = new
        // ImageIcon(Connection.class.getResource("assets/connect-button.jpg"));
        submitAction = new SubmitConnectionAction();
        getmodsAction = new GetModsAction();
    }

    protected class ConnectionPanel extends JPanel {
        public void focusInput() {
            username.requestFocus();
            username.requestFocusInWindow();
        }

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
            errorMessage.setOpaque(false);
            errorMessage.setForeground(new Color(255, 255, 255));
            username = new RoundJTextField(17);
            username.setHorizontalAlignment(SwingConstants.CENTER);
            username.setBorder(new GuiRoundedBorder(10));
            Font normalFont = GuiTools.getFont("Monospaced", Font.PLAIN, 16, username.getFont());
            username.setFont(normalFont);
            username.setCaretColor(GuiPanelMainChatSystem.almostWhiteColor);
            username.setForeground(GuiPanelMainChatSystem.almostWhiteColor);
            username.setBackground(GuiPanelMainChatSystem.chatBGColor);
            username.addKeyListener(new KeyListener() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        new SubmitConnectionAction().actionPerformed(null);
                    }
                }
    
                @Override
                public void keyReleased(KeyEvent arg0) {
                }
    
                @Override
                public void keyTyped(KeyEvent arg0) {
                }
            });
            username.requestFocus();
            username.requestFocusInWindow();
            connect = new JButton(submitAction);
            connect.setBorder(new GuiRoundedBorder(10, 0, 52, 0, 52));
            connect.setBackground(GuiPanelMainChatSystem.headerFooterBGColor);
            connect.setForeground(GuiPanelMainChatSystem.almostWhiteColor);
            connect.setMargin(new Insets(10, 10, 10, 10));
            connect.setFont(boldFont);
            final JSButtonUI ui = new JSButtonUI();
            ui.setPressedColor(connect.getBackground().darker());
            connect.setUI(ui);
            connect.setCursor(new Cursor(Cursor.HAND_CURSOR));
            connect.setHorizontalAlignment(SwingConstants.CENTER);

            // MODS
            mods = new JButton(getmodsAction);
            mods.setBorder(new EmptyBorder(10, 10, 10, 10));
            mods.setBackground(GuiPanelMainChatSystem.headerFooterBGColor);
            mods.setForeground(GuiPanelMainChatSystem.almostWhiteColor);
            mods.setMargin(new Insets(10, 10, 10, 10));
            Font modsFont = GuiTools.getFont("Monospaced", Font.ITALIC, 16, username.getFont());
            mods.setFont(modsFont);
            ui.setPressedColor(mods.getBackground().darker());
            mods.setUI(ui);
            mods.setCursor(new Cursor(Cursor.HAND_CURSOR));
            mods.setHorizontalAlignment(SwingConstants.CENTER);

            JPanel panel0 = new JPanel();
            panel0.setOpaque(false);
            panel0.add(errorMessage);

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

            JPanel panel3 = new JPanel();
            panel3.setOpaque(false);
            panel3.add(mods);

            setLayout(new GridLayout(4, 0));

            add(JIRSendLogo);
            panel2.add(connect);
            if (window.lastError != "") {
                panel2.add(errorMessage);
                errorMessage.setVisible(true);
                window.lastError = "";
            }
            add(panel1);
            add(panel2);
            add(panel3);
        }
    }

    private class SubmitConnectionAction extends AbstractAction {
        public SubmitConnectionAction() {
            super("Connection");
        }

        public void actionPerformed(ActionEvent action) {
            String usernameAsked = username.getText().trim();
            String res = window.controller.changeUsername(usernameAsked);
            if (res.equals("")) {
                window.switchToNextSection();
            } else {
                ErrorPopup.show("Connection impossible", res);
                window.lastError = res;
                window.refreshSection();
                return;
            }

            Log.l("Connecting as '" + usernameAsked + "'", Log.LOG);
        }
    }

    private class GetModsAction extends AbstractAction {
        public GetModsAction() {
            super("No mods loaded");
        }

        public void actionPerformed(ActionEvent action) {
            ModsPopup.show(window.controller.modc.getModsInformation());
        }
    }
}
