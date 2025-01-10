package com.JIRSendApp.view.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;

public class GUISectionLoading extends GUISection {
    private JLabel JIRSendLogo;

    public GUISectionLoading(MainGUI window, Frame frame) {
        super(window, frame, "Connection");
    }

    public JPanel createPanel() {
        JPanel panel = new LoadingPanel();
        return panel;
    }

    protected void createActions() {
    }

    private class LoadingPanel extends JPanel {
        public LoadingPanel() {
            super(new BorderLayout());
            setBorder(new EmptyBorder(10, 40, 10, 40));
            setBackground(GuiPanelMainChatSystem.bodyBGColor);
            setForeground(GuiPanelMainChatSystem.whitestColor);

            JIRSendLogo = new JLabel(
                    new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/jirsend_logo.png"))
                            .getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH)));
            JIRSendLogo.setBorder(BorderFactory.createEmptyBorder(50, 10, 10, 10));

            JLabel loadingLabel = new JLabel("<html><body><p style=\"width:180px; padding:10px; border-radius:10px; text-align:center;\">"
            + "JIRSend is starting<br>"
            + "<span style=\"color:gray;font-size:smaller;\">Please wait</span>"
            +"</p></body></html>");
            Font boldFont = GuiTools.getFont("Monospaced", Font.BOLD, 16, loadingLabel.getFont());
            loadingLabel.setFont(boldFont);
            loadingLabel.setForeground(GuiPanelMainChatSystem.almostWhiteColor);
            JLabel errorMessage = new JLabel(
                    "<html><body><p style=\"width:180px; background:red; padding:10px; border-radius:10px; text-align:center;\">"
                            + window.lastError + "</p></body></html>",
                    SwingConstants.CENTER);
            errorMessage.setOpaque(false);
            errorMessage.setForeground(new Color(255, 255, 255));

            JPanel panel0 = new JPanel();
            panel0.setOpaque(false);
            // panel0.setOpaque(false);
            panel0.add(errorMessage);
            // panel0.set

            JPanel panel1 = new JPanel();
            JPanel innerPanel1 = new JPanel();
            innerPanel1.setLayout(new GridLayout(1, 0, 5, 5));
            innerPanel1.add(loadingLabel);
            innerPanel1.setOpaque(false);
            innerPanel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            panel1.add(innerPanel1);
            panel1.setOpaque(false);

            JPanel panel2 = new JPanel();
            panel2.setOpaque(false);

            setLayout(new GridLayout(2, 0));

            add(JIRSendLogo);
            add(panel1);// , BorderLayout.SOUTH);
        }
    }
}
