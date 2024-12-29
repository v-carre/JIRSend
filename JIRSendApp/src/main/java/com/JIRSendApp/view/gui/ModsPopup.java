package com.JIRSendApp.view.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.JIRSendAPI.JIRSendMod.JIRSendModInformation;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ModsPopup {
    private static JPanel modList;
    private final static int POPUP_WIDTH = 400;

    public static void show(ArrayList<JIRSendModInformation> mods) {
        JDialog dialog = new JDialog((Frame) null, "JIRSend mods loaded", true);
        dialog.setUndecorated(false);
        dialog.setAlwaysOnTop(false);
        // dialog.setLayout(null);

        JLabel messageLabel = new JLabel("Loaded mods (" + mods.size() + ")");
        messageLabel.setForeground(GuiPanelMainChatSystem.whitestColor);
        messageLabel.setBackground(GuiPanelMainChatSystem.bodyBGColor);
        messageLabel.setMaximumSize(new Dimension(1920, 40));
        messageLabel.setOpaque(true);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JTextPane contentLabel = new JTextPane();
        contentLabel.setContentType("text/html");
        contentLabel.setText(
                "<html><div style='text-align: center; color:white; font-family:monospaced;'>To load mods, create a directory named 'mods' where your application is. Then put your .jar mods in it.</div></html>");
        contentLabel.setEditable(false);
        contentLabel.setForeground(GuiPanelMainChatSystem.whitestColor);
        contentLabel.setBackground(GuiPanelMainChatSystem.bodyBGColor);
        contentLabel.setOpaque(true);

        modList = new JPanel();
        modList.setLayout(new BoxLayout(modList, BoxLayout.Y_AXIS));
        modList.setSize(new Dimension(POPUP_WIDTH, 10));
        modList.setAlignmentY(Component.TOP_ALIGNMENT);
        modList.removeAll();
        JScrollPane modListScroll = new JScrollPane();
        modListScroll.getVerticalScrollBar().setUI(new JSScrollBarUI());
        modListScroll.setBackground(GuiPanelMainChatSystem.contactSectionBGColor);
        modList.setBackground(GuiPanelMainChatSystem.contactSectionBGColor);
        // modList.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        modList.setBorder(null);
        modListScroll.setViewportView(modList);
        modListScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        modListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        modListScroll.getVerticalScrollBar().setUnitIncrement(6);

        modListScroll.getViewport().setBorder(null);

        for (JIRSendModInformation jirSendModInformation : mods) {
            createContactElement(jirSendModInformation);
        }

        JButton okButton = new JButton("Close");
        okButton.setForeground(GuiPanelMainChatSystem.whitestColor);
        okButton.setBackground(GuiPanelMainChatSystem.chatBGColor);
        okButton.setFocusPainted(false);
        okButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        okButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        okButton.setContentAreaFilled(false);
        okButton.setOpaque(true);
        okButton.setMaximumSize(new Dimension(1920, 80));
        okButton.setBorder(new RoundedBorder(15));

        okButton.addActionListener(e -> dialog.dispose());

        dialog.setLayout(new BorderLayout());

        JPanel subdialog = new JPanel();
        // subdialog.setLayout(new GridLayout(3, 0));
        subdialog.setLayout(new BorderLayout());
        subdialog.add(messageLabel, BorderLayout.NORTH);
        if (mods.size() > 0)
            subdialog.add(modListScroll, BorderLayout.CENTER);
        else
            subdialog.add(contentLabel, BorderLayout.CENTER);
        subdialog.add(okButton, BorderLayout.SOUTH);
        dialog.add(subdialog);

        // dialog.setMinimumSize(new Dimension(300, 150));
        // dialog.setMaximumSize(new Dimension(1000, 900));
        dialog.setSize(POPUP_WIDTH, 500);
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

    private static void createContactElement(JIRSendModInformation modInfo) {
        JPanel modElement = new JPanel();
        // modElement.setMinimumSize(new Dimension(50, 20));
        // modElement.setPreferredSize(new Dimension(300, 500));
        // modElement.setMaximumSize(new Dimension(POPUP_WIDTH, 800));
        modElement.setCursor(new Cursor(Cursor.HAND_CURSOR));
        modElement.setBackground(GuiPanelMainChatSystem.contactElementBGColor);
        // modElement.setBorder(new GuiRoundedBorder(10));
        // modElement.setLayout(new GridBagLayout());
        modElement.setLayout(new GridLayout(3, 1));
        modElement.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                modElement.setBackground(GuiPanelMainChatSystem.contactElementBGColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                modElement.setBackground(GuiPanelMainChatSystem.contactElementBGColor);
            }
        });
        modList.add(modElement);
        JLabel modName = new JLabel();
        modName.setForeground(GuiPanelMainChatSystem.almostWhiteColor);
        modName.setText("<html><body style=\"margin: 0; padding: 10; text-align:center;\">"
                + modInfo.name
                + " <span style=\"font-size:smaller; color:gray;\">" + modInfo.getVersion() + "</span>"
                + "</body></html>");
        modName.setBorder(null);
        modElement.add(modName);

        JLabel modIcon = new JLabel(new ImageIcon(modInfo.modIcon.getImage()
                .getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
        modElement.add(modIcon);

        JTextPane descriptionLabel = new JTextPane();
        descriptionLabel.setContentType("text/html");
        descriptionLabel.setText(
                "<html><div style='margin: 0; padding: 10; text-align: justify; font-size:smaller; color:white; font-family:monospaced; text-wrap: wrap;'>"
                        + modInfo.description
                        + "</div></html>");
        descriptionLabel.setEditable(false);
        descriptionLabel.setOpaque(false);
        descriptionLabel.setBorder(null);
        modElement.add(descriptionLabel);

        // GridBagConstraints gbc = new GridBagConstraints();

        // gbc.gridx = 0;
        // gbc.gridy = 0;
        // gbc.anchor = GridBagConstraints.NORTHWEST;
        // // gbc.insets = new Insets(5, 5, 5, 5);
        // gbc.insets = new Insets(0, 0, 0, 0);
        // modElement.add(modName, gbc);

        // gbc.gridx = 0;
        // gbc.gridy = 1;
        // gbc.anchor = GridBagConstraints.SOUTHWEST;
        // modElement.add(modIcon, gbc);

        // gbc.gridx = 1;
        // gbc.gridy = 0;
        // gbc.gridheight = 2; // Span across two rows (right, spanning vertically)
        // gbc.anchor = GridBagConstraints.EAST;
        // gbc.fill = GridBagConstraints.BOTH; // let the descriptionLabel take up extra space
        // modElement.add(descriptionLabel, gbc);

        // modElement.setAlignmentX(Component.CENTER_ALIGNMENT);
        // modElement.setAlignmentY(Component.TOP_ALIGNMENT);
        // modElement.setPreferredSize(new Dimension(300, 100));

        // modElement.setBackground(Color.GREEN);
        // modName.setBackground(Color.BLUE);
        // modIcon.setBackground(Color.RED);
        // descriptionLabel.setBackground(Color.YELLOW);
    }

}
