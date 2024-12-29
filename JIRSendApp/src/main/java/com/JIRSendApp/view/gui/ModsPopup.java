package com.JIRSendApp.view.gui;

import javax.swing.*;

import com.JIRSendAPI.JIRSendMod.JIRSendModInformation;

import java.awt.*;
import java.util.ArrayList;

public class ModsPopup {
    public static void show(ArrayList<JIRSendModInformation> mods) {
        JDialog dialog = new JDialog((Frame) null, "JIRSend mods loaded", true);
        dialog.setUndecorated(false);
        dialog.setAlwaysOnTop(false);

        JLabel messageLabel = new JLabel("Loaded mods (" + mods.size() + ")");
        messageLabel.setForeground(GuiPanelMainChatSystem.whitestColor);
        messageLabel.setBackground(GuiPanelMainChatSystem.bodyBGColor);
        messageLabel.setOpaque(true);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JTextPane contentLabel = new JTextPane();
        contentLabel.setContentType("text/html"); 
        contentLabel.setText("<html><div style='text-align: center; color:white; font-family:monospaced;'>To load mods, create a directory named 'mods' where your application is. Then put your .jar mods in it.</div></html>");
        contentLabel.setEditable(false);
        contentLabel.setForeground(GuiPanelMainChatSystem.whitestColor);
        contentLabel.setBackground(GuiPanelMainChatSystem.bodyBGColor);
        contentLabel.setOpaque(true);


        JButton okButton = new JButton("Close");
        okButton.setForeground(GuiPanelMainChatSystem.whitestColor);
        okButton.setBackground(GuiPanelMainChatSystem.chatBGColor);
        okButton.setFocusPainted(false);
        okButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        okButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        okButton.setContentAreaFilled(false);
        okButton.setOpaque(true);

        okButton.setBorder(new RoundedBorder(15));

        okButton.addActionListener(e -> dialog.dispose());

        dialog.setLayout(new BorderLayout());

        JPanel subdialog = new JPanel();
        subdialog.setLayout(new GridLayout(3, 0));
        // dialog.add(errorLabel, BorderLayout.NORTH);
        subdialog.add(messageLabel);
        subdialog.add(contentLabel);
        subdialog.add(okButton, BorderLayout.SOUTH);
        dialog.add(subdialog);

        dialog.setMinimumSize(new Dimension(300, 150));
        dialog.setMaximumSize(new Dimension(1000, 900));
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(null); // Center on screen
        dialog.getContentPane().setBackground(GuiPanelMainChatSystem.bodyBGColor);

        dialog.requestFocus();
        dialog.setVisible(true);
    }

    // Helper class to create rounded borders
    static class RoundedBorder extends javax.swing.border.AbstractBorder {
        private final int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(c.getForeground());
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(10, 10, 10, 10);
        }
    }
}
