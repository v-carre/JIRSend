package com.JIRSend.gui;

import javax.swing.*;
import java.awt.*;

public class ErrorPopup {
    public static void show(String title, String message) {
        JDialog dialog = new JDialog((Frame) null, "Error: " + title, true);
        dialog.setUndecorated(true); // Removes window decorations
        dialog.setAlwaysOnTop(true); // Keeps the dialog on top of other windows

        JLabel errorLabel = new JLabel("ERROR!");
        errorLabel.setForeground(GuiPanelMainChatSystem.whitestColor);
        errorLabel.setBackground(Color.RED);
        errorLabel.setOpaque(true);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel messageLabel = new JLabel(message);
        messageLabel.setForeground(GuiPanelMainChatSystem.whitestColor);
        messageLabel.setBackground(GuiPanelMainChatSystem.bodyBGColor);
        messageLabel.setOpaque(true);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton okButton = new JButton("OK");
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
        dialog.add(errorLabel, BorderLayout.NORTH);
        dialog.add(messageLabel, BorderLayout.CENTER);
        dialog.add(okButton, BorderLayout.SOUTH);

        dialog.setSize(300, 150);
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
