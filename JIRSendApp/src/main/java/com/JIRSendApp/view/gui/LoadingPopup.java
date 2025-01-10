package com.JIRSendApp.view.gui;

import javax.swing.*;

import java.awt.*;
public class LoadingPopup {
    private final static int POPUP_WIDTH = 400;
    private static JDialog dialog;

    public static void start() {
        dialog  = new JDialog((Frame) null, "Loading JIRSend", true);
        dialog.setUndecorated(false);
        dialog.setAlwaysOnTop(false);
        // dialog.setLayout(null);

        JLabel messageLabel = new JLabel("<html><body><p style=\"width:180px; padding:10px; border-radius:10px; text-align:center;\">"
        + "JIRSend is starting<br>"
        + "<span style=\"color:gray;font-size:smaller;\">Please wait</span>"
        +"</p></body></html>");
        messageLabel.setForeground(GuiPanelMainChatSystem.whitestColor);
        messageLabel.setBackground(GuiPanelMainChatSystem.bodyBGColor);
        messageLabel.setMaximumSize(new Dimension(1920, 40));
        messageLabel.setOpaque(true);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        dialog.setLayout(new BorderLayout());

        JPanel subdialog = new JPanel();
        // subdialog.setLayout(new GridLayout(3, 0));
        subdialog.setLayout(new BorderLayout());
        subdialog.add(messageLabel, BorderLayout.NORTH);
        subdialog.setBackground(GuiPanelMainChatSystem.bodyBGColor);
        dialog.setBackground(GuiPanelMainChatSystem.bodyBGColor);
        dialog.add(subdialog);

        // dialog.setMinimumSize(new Dimension(300, 150));
        // dialog.setMaximumSize(new Dimension(1000, 900));
        dialog.setSize(POPUP_WIDTH, 500);
        dialog.setLocationRelativeTo(null); // Center on screen
        dialog.getContentPane().setBackground(GuiPanelMainChatSystem.bodyBGColor);

        dialog.requestFocus();
        dialog.setVisible(true);
    }

    public static void stop() {
        dialog.setVisible(false);
        dialog.dispose();
    }
}
