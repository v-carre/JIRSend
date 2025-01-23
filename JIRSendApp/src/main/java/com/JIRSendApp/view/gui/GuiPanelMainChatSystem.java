package com.JIRSendApp.view.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;

import com.JIRSendApp.controller.MainController;
import com.JIRSendApp.model.Message;
import com.JIRSendApp.model.user.BaseUser;
import com.JIRSendApp.model.user.Conversation;
import com.JIRSendApp.model.user.UserEntry;
import com.JIRSendApp.model.user.UserEntry.Status;
import com.JIRSendApp.view.cli.Log;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.Collections;
import java.util.Locale;

public class GuiPanelMainChatSystem {
    private JPanel contentPane;
    private JPanel headPane;
    private JPanel bodyPane;
    private JPanel footer;
    private JPanel contactsSection;
    private JPanel chatSection;
    private JPanel chatContent;
    private JPanel contactsContent;
    private JLabel contactsLabel;
    private JPanel chatContactName;
    private JPanel SendMessageSection;
    private JScrollPane messagesScroll;
    private JButton sendMessageButton;
    private JScrollPane messageToSendScroll;
    private JTextPane inputMessage;
    private JLabel chatContactLabel;
    private JTextField usernameTextField;
    private JScrollPane contactsListScroll;
    private JPanel contactsList;
    private JButton reconnectButton;
    private JPanel messagesList;
    private JLabel contactName;
    private JLabel messageAuthor;
    private JLabel messageTime;
    private JTextPane messageContent;
    private JLabel chatSystemName;
    private JLabel JIRSendLogo;
    private RoundJTextField contactSearch;

    private MainController controller;

    public static final Color headerFooterBGColor = new Color(-11842224);
    public static final Color bodyBGColor = new Color(-14342358);
    public static final Color contactSectionBGColor = new Color(-13223877);
    public static final Color contactElementBGColor = new Color(-13288643);
    public static final Color whitestColor = new Color(-394241);
    public static final Color grayColor = new Color(150, 150, 150);
    public static final Color disconnectedColor = new Color(255, 180, 180);
    public static final Color almostWhiteColor = new Color(-854792);
    public static final Color messageBGColor = new Color(-13816014);
    public static final Color chatBGColor = new Color(-14671323);
    public static final Color headerContactColor = chatBGColor;
    public static final Color carretColor = new Color(-3684409);

    private MainGUI maingui;

    private Action submitSUAction = new SubmitSwitchUsernameAction();
    private Action submitMsgAction = new SubmitMessageAction();

    public GuiPanelMainChatSystem(MainController controller, MainGUI maingui) {
        this.controller = controller;
        usernameTextField.setText(this.controller.getUsername());
        this.maingui = maingui;

        MainController.contactsChange.subscribe((event) -> {
            updateGUI();
        });
        MainController.messageReceived.subscribe((msg) -> {
            updateGUI();
        });
        MainController.sendMessage.subscribe((msg) -> {
            updateGUI();
        });
        updateGUI();
    }

    private void updateGUI() {
        updateConversation();
        updateContactList();
        maingui.updateIcon();
        maingui.refreshFrame();
    }

    private void updateContactList() {
        String currentConvName = controller.getConversationName();
        contactsList.removeAll();
        var list = controller.getContacts();
        Collections.sort(list, (a, b) -> {
            if (a.online() && !b.online())
                return -1;
            if (b.online() && !a.online())
                return 1;
            Message ma = controller.getConversationLastMessage(a.username);
            Message mb = controller.getConversationLastMessage(b.username);
            if (ma == null && mb == null)
                return a.username.compareTo(b.username);
            if (mb == null)
                return -1;
            if (ma == null)
                return 1;
            return mb.time.compareTo(ma.time);

        });
        for (UserEntry ue : list) {
            if (contactSearch == null || contactSearch.getText() == ""
                    || ue.username.toLowerCase().contains(contactSearch.getText().toLowerCase()))
                createContactElement(ue.username, ue.online, ue.icon, currentConvName == ue.username, false);
        }
    }

    private void updateConversation() {
        messagesList.removeAll();
        Conversation conv = controller.getConversation();
        if (conv == null) {
            chatContactLabel.setText("<- Choose a conversation");
            createMessageElement("JIRSend", " ", "Welcome in JIRSend!\n\n"
                    + "- In the left panel are shown the connected users. You can simply click on them to start a conversation with them.\n"
                    + "You will see a text input at the bottom to send a message.\n\n"
                    + "- To change your username, just modify your username in the footer and click on the refresh button.");
            SendMessageSection.setVisible(false);
            return;
        }
        String recipient = controller.getConversationName();
        String you = controller.getUsername();
        chatContactLabel.setText(recipient);
        ImageIcon convIcon = controller.getConversationIcon();
        chatContactName.removeAll();
        if (convIcon != null) {
            JLabel convIconLabel = new JLabel(new ImageIcon(convIcon.getImage()
                    .getScaledInstance(40, 40, Image.SCALE_SMOOTH)));
            chatContactName.add(convIconLabel,
                    new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                            com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
                            com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                            null, null, 1, false));
            chatContactName.add(chatContactLabel,
                    new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1,
                            com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
                            com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                            null, null, 1, false));
        } else {
            chatContactName.add(chatContactLabel,
                    new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1,
                            com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                            com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                            com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                            null, null, 1, false));
        }
        if (controller.isConnected(recipient))
            SendMessageSection.setVisible(true);
        else
            SendMessageSection.setVisible(false);
        int msgNb = 0;
        int unReadThreshold = conv.getMessages().size() - conv.numberUnRead();
        for (Message msg : conv.getMessages()) {
            if (msgNb == unReadThreshold && conv.numberUnRead() > 0)
                createUnreadBar();
            if (msg.sender.equals(BaseUser.youString) || msg.sender.equals(BaseUser.senderString))
                createMessageElement(msg.sender.equals(BaseUser.youString) ? you : recipient, msg.time, msg.message);
            else
                createMessageElement(msg.sender, msg.time, msg.message);
            msgNb++;
        }
        controller.markConversationRead(recipient);
        JScrollBar vertical = messagesScroll.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    private void createContactElement(String username, Status status, ImageIcon icon, boolean currentConv,
            boolean hasNewMessage) {
        JPanel contactElement = new JPanel();
        contactElement.setMinimumSize(new Dimension(50, 5));
        contactElement.setMaximumSize(new Dimension(1920, 100));
        contactElement.setLayout(new GridLayout(2, 1));
        contactElement.setCursor(new Cursor(Cursor.HAND_CURSOR));
        contactElement.setBackground(
                currentConv ? contactElementBGColor.brighter().brighter()
                        : (status != Status.Offline ? contactElementBGColor.brighter()
                                : contactElementBGColor));
        contactElement.setBorder(null);
        contactElement.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.getConversation(username);
                updateGUI();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (status != Status.Offline)
                    contactElement
                            .setBackground(contactElementBGColor.brighter().brighter());
                else
                    contactElement
                            .setBackground(currentConv
                                    ? contactElementBGColor.brighter().brighter()
                                    : contactElementBGColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                contactElement.setBackground(
                        currentConv ? contactElementBGColor.brighter().brighter()
                                : (status != Status.Offline
                                        ? contactElementBGColor.brighter()
                                        : contactElementBGColor));
            }
        });
        contactsList.add(contactElement);
        contactName = new JLabel();
        Font contactNameFont = this.getFont("Monospaced", Font.BOLD, -1, contactName.getFont());
        if (contactNameFont != null)
            contactName.setFont(contactNameFont);
        contactName.setForeground(status != Status.Offline ? almostWhiteColor : disconnectedColor);
        int nbUnread = controller.getConversationUnreadNumber(username);
        contactName.setText("<html><body style=\"text-align:center;\">" + username
                + displayStatus(status)
                + (nbUnread > 0 ? ("<br><span style=\"color:white;background:red;\">(" + nbUnread
                        + ")</span>")
                        : "")
                + "</body></html>");

        if (icon != null) {
            JLabel modIcon = new JLabel(new ImageIcon(icon.getImage()
                    .getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
            modIcon.setBorder(null);
            contactElement.add(modIcon);
        } else {
            JLabel modIcon = new JLabel(new ImageIcon(
                    new javax.swing.ImageIcon(getClass().getResource("/assets/jirsend_logo.png")).getImage()
                            .getScaledInstance(20, 20, Image.SCALE_SMOOTH)));

            // new JLabel(new ImageIcon(icon.getImage()
            // .getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
            modIcon.setBorder(null);
            contactElement.add(modIcon);
        }
        JPanel subP = new JPanel();
        subP.setOpaque(false);
        subP.add(contactName);
        contactName.setBorder(null);
        contactElement.setBorder(null);
        contactElement.add(subP);
    }

    private void createUnreadBar() {
        JPanel unreadBar = new JPanel();

        unreadBar.setMinimumSize(new Dimension(50, 5));
        unreadBar.setMaximumSize(new Dimension(1920, 5));
        unreadBar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        unreadBar.setBackground(new Color(255, 0, 0));
        messagesList.add(unreadBar);
    }

    private void createMessageElement(String author, String time, String content) {
        JPanel messageElement = new JPanel();
        messageElement
                .setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3,
                        new Insets(0, 0, 0, 0), -1, -1));
        messageElement.setBackground(messageBGColor);
        messagesList.add(messageElement);
        messageAuthor = new JLabel();
        messageAuthor.setBackground(messageBGColor);
        Font messageAuthorFont = this.getFont("Monospaced", Font.BOLD, -1, messageAuthor.getFont());
        if (messageAuthorFont != null)
            messageAuthor.setFont(messageAuthorFont);
        messageAuthor.setForeground(whitestColor);
        messageAuthor.setText(author + ": ");
        messageAuthor.setBorder(new EmptyBorder(3, 3, 3, 3));
        messageElement.add(messageAuthor,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTHWEST,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        null, null, 0, false));
        messageContent = new JTextPane();
        messageContent.setBackground(messageBGColor);
        messageContent.setDisabledTextColor(almostWhiteColor);
        messageContent.setEditable(false);
        messageContent.setEnabled(false);
        Font messageContentFont = this.getFont("Monospaced", -1, -1, messageContent.getFont());
        if (messageContentFont != null)
            messageContent.setFont(messageContentFont);
        messageContent.setForeground(almostWhiteColor);
        messageContent.setSelectedTextColor(whitestColor);
        messageContent.setText(content);
        messageElement.add(messageContent,
                new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null,
                        new Dimension(150, 50),
                        null, 0, false));
        messageTime = new JLabel();
        messageTime.setBackground(messageBGColor);
        Font messageTimeFont = this.getFont("Monospaced", Font.ITALIC, -1, messageTime.getFont());
        if (messageTimeFont != null)
            messageTime.setFont(messageTimeFont);
        messageTime.setForeground(grayColor);
        messageTime.setText(time);
        messageElement.add(messageTime,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_SOUTHWEST,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        null, null, 0, false));
        messageElement.setBorder(new EmptyBorder(9, 9, 9, 9));
    }

    public JPanel getPanel() {
        return contentPane;
    }

    {
        setupUI();
    }

    private void setupUI() {
        contentPane = new JPanel();
        contentPane.setLayout(
                new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), 0, 0));
        headPane = new JPanel();
        headPane.setLayout(
                new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), 0, 0));
        headPane.setBackground(headerFooterBGColor);
        contentPane.add(headPane,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        new Dimension(24, 25),
                        null, 0, false));
        chatSystemName = new JLabel();
        chatSystemName.setOpaque(false);
        Font chatSystemNameFont = this.getFont("Monospaced", Font.BOLD, -1, chatSystemName.getFont());
        if (chatSystemNameFont != null)
            chatSystemName.setFont(chatSystemNameFont);
        chatSystemName.setForeground(whitestColor);
        chatSystemName.setText("JIRSend");
        chatSystemName.setBorder(new EmptyBorder(0, 10, 0, 0));
        headPane.add(chatSystemName,
                new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        null, null, 0, false));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        headPane.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1,
                com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL,
                com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null,
                0, false));
        JIRSendLogo = new JLabel(new ImageIcon(
                new javax.swing.ImageIcon(getClass().getResource("/assets/jirsend_logo.png")).getImage()
                        .getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        JIRSendLogo.setHorizontalAlignment(0);
        JIRSendLogo.setHorizontalTextPosition(0);
        JIRSendLogo.setBorder(new EmptyBorder(0, 1, 0, 0));
        headPane.add(JIRSendLogo,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        new Dimension(20, 20),
                        null, 0, false));
        bodyPane = new JPanel();
        bodyPane.setLayout(
                new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 0, 0));
        bodyPane.setBackground(bodyBGColor);
        contentPane.add(bodyPane,
                new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, new Dimension(24, 353), null, 0, false));
        contactsSection = new JPanel();
        contactsSection.setLayout(new CardLayout(0, 0));
        contactsSection.setBackground(contactSectionBGColor);
        bodyPane.add(contactsSection,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        new Dimension(121, 0),
                        null, 0, false));
        contactsContent = new JPanel();
        contactsContent
                .setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1,
                        new Insets(0, 0, 0, 0), -1, -1));
        contactsContent.setBackground(contactSectionBGColor);
        contactsSection.add(contactsContent, "Card1");
        contactsLabel = new JLabel();
        contactsLabel.setBackground(contactElementBGColor);
        Font contactsLabelFont = this.getFont("Monospaced", Font.BOLD, 22, contactsLabel.getFont());
        if (contactsLabelFont != null)
            contactsLabel.setFont(contactsLabelFont);
        contactsLabel.setForeground(whitestColor);
        contactsLabel.setText("Contacts");
        contactsContent.add(contactsLabel,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_NORTH,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        null, null, 0, false));
        contactSearch = new RoundJTextField(17);
        contactSearch.setHorizontalAlignment(SwingConstants.CENTER);
        contactSearch.setBorder(new GuiRoundedBorder(10));
        Font normalFont = GuiTools.getFont("Monospaced", Font.PLAIN, 8, contactSearch.getFont());
        contactSearch.setFont(normalFont);
        contactSearch.setCaretColor(almostWhiteColor);
        contactSearch.setForeground(almostWhiteColor);
        contactSearch.setBackground(chatBGColor);
        contactSearch.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent arg0) {
                updateContactList();
                maingui.refreshFrame();
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
            }
        });
        contactsContent.add(contactSearch,
                new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        null, null, 0, false));
        contactsListScroll = new JScrollPane();
        contactsListScroll.getVerticalScrollBar().setUI(new JSScrollBarUI());
        contactsListScroll.setBackground(contactSectionBGColor);
        contactsContent.add(contactsListScroll,
                new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                        null, null, null, 0, false));
        contactsList = new JPanel();
        contactsList.setLayout(new BoxLayout(contactsList, BoxLayout.Y_AXIS));
        contactsList.setAlignmentY(Component.TOP_ALIGNMENT);

        contactsList.setBackground(contactSectionBGColor);
        contactsListScroll.setViewportView(contactsList);
        contactsListScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        contactsListScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contactsListScroll.getVerticalScrollBar().setUnitIncrement(9);
        chatSection = new JPanel();
        chatSection.setLayout(new CardLayout(0, 0));
        chatSection.setBackground(chatBGColor);
        bodyPane.add(chatSection,
                new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, null, null, 0, false));
        chatContent = new JPanel();
        chatContent.setLayout(new BorderLayout());
        chatContent.setBackground(chatBGColor);
        chatSection.add(chatContent, "Card1");
        chatContactName = new JPanel();
        chatContactName
                .setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2,
                        new Insets(0, 0, 0, 0), -1, -1));
        chatContactName.setBackground(headerContactColor);
        chatContactName.setBorder(new MatteBorder(0, 0, 2, 0, headerContactColor.brighter()));
        chatContactName.setEnabled(true);
        chatContent.add(chatContactName, BorderLayout.NORTH);
        chatContactLabel = new JLabel();
        Font chatContactLabelFont = this.getFont("Monospaced", Font.BOLD, 36, chatContactLabel.getFont());
        if (chatContactLabelFont != null)
            chatContactLabel.setFont(chatContactLabelFont);
        chatContactLabel.setForeground(whitestColor);
        chatContactLabel.setIconTextGap(10);
        chatContactLabel.setText("<- Choose a conversation");
        chatContactName.add(chatContactLabel,
                new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        null, null, 1, false));
        SendMessageSection = new JPanel();
        SendMessageSection
                .setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2,
                        new Insets(0, 0, 0, 0), -1, -1));
        SendMessageSection.setBackground(messageBGColor.brighter());
        chatContent.add(SendMessageSection, BorderLayout.SOUTH);

        sendMessageButton = new JButton(submitMsgAction);
        sendMessageButton.setText("");
        sendMessageButton.setIcon(
                new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/send.png"))
                        .getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH)));
        sendMessageButton.setBackground(messageBGColor.brighter());
        sendMessageButton.setPreferredSize(new Dimension(60, 60));
        sendMessageButton.setBorderPainted(true);
        sendMessageButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        final JSButtonUI uiSend = new JSButtonUI();
        uiSend.setPressedColor(sendMessageButton.getBackground().darker());
        sendMessageButton.setUI(uiSend);
        sendMessageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Font sendMessageButtonFont = this.getFont("Monospaced", Font.BOLD, 8,
                sendMessageButton.getFont());
        if (sendMessageButtonFont != null)
            sendMessageButton.setFont(sendMessageButtonFont);
        sendMessageButton.setForeground(almostWhiteColor);
        SendMessageSection.add(sendMessageButton,
                new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        null, null, 0, false));
        messageToSendScroll = new JScrollPane();
        messageToSendScroll.getVerticalScrollBar().setUI(new JSScrollBarUI());
        messageToSendScroll.setBorder(null);
        messageToSendScroll.setBackground(messageBGColor.brighter());
        SendMessageSection.add(messageToSendScroll,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK
                                | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                        null, null, null, 0, false));
        inputMessage = new JTextPane();
        inputMessage.setBackground(messageBGColor.brighter());
        inputMessage.setBorder(new EmptyBorder(8, 8, 8, 8));
        inputMessage.setMargin(new Insets(8, 8, 8, 8));
        inputMessage.setCaretColor(carretColor);
        inputMessage.setDragEnabled(true);
        Font inputMessageFont = this.getFont("Monospaced", -1, -1, inputMessage.getFont());
        if (inputMessageFont != null)
            inputMessage.setFont(inputMessageFont);
        inputMessage.setForeground(almostWhiteColor);
        inputMessage.setName("Enter your message here");
        inputMessage.setSelectedTextColor(whitestColor);
        inputMessage.setText("");
        inputMessage.setToolTipText("Enter your message here");
        inputMessage.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && (e.isShiftDown() || e.isControlDown())) {
                    new SubmitMessageAction().actionPerformed(null);
                }
            }

            @Override
            public void keyReleased(KeyEvent arg0) {
            }

            @Override
            public void keyTyped(KeyEvent arg0) {
            }
        });
        messageToSendScroll.setViewportView(inputMessage);
        messageToSendScroll.getVerticalScrollBar().setUnitIncrement(4);

        messagesScroll = new JScrollPane();
        messagesScroll.getVerticalScrollBar().setUI(new JSScrollBarUI());
        messagesScroll.setAutoscrolls(true);
        messagesScroll.setBackground(chatBGColor);
        messagesScroll.setHorizontalScrollBarPolicy(31);
        messagesScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        chatContent.add(messagesScroll, BorderLayout.CENTER);
        messagesList = new JPanel();

        messagesList.setLayout(new BoxLayout(messagesList, BoxLayout.Y_AXIS));
        messagesList.setAlignmentY(Component.TOP_ALIGNMENT);
        messagesList.setBorder(null);
        messagesList.setBackground(chatBGColor);
        messagesList.setForeground(almostWhiteColor);
        messagesScroll.setViewportView(messagesList);
        messagesScroll.getVerticalScrollBar().setUnitIncrement(10);

        footer = new JPanel();
        footer.setLayout(
                new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), 0, 0));
        footer.setBackground(headerFooterBGColor);
        contentPane.add(footer,
                new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        new Dimension(24, 25),
                        null, 0, false));
        usernameTextField = new RoundJTextField(17);
        usernameTextField.setBackground(headerFooterBGColor.darker());
        usernameTextField.setCaretColor(carretColor);
        Font usernameTextFieldFont = this.getFont("Monospaced", Font.BOLD, -1,
                usernameTextField.getFont());
        if (usernameTextFieldFont != null)
            usernameTextField.setFont(usernameTextFieldFont);
        usernameTextField.setForeground(almostWhiteColor);
        usernameTextField.setHorizontalAlignment(4);
        usernameTextField.setSelectedTextColor(whitestColor);
        usernameTextField.setText("---");
        usernameTextField.setToolTipText("Change your username");
        footer.add(usernameTextField,
                new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_EAST,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_NONE,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        new Dimension(150, -1),
                        new Dimension(250, -1), 0, false));
        reconnectButton = new JButton(submitSUAction);
        reconnectButton.setText("");
        reconnectButton.setIcon(
                new ImageIcon(new javax.swing.ImageIcon(getClass().getResource("/assets/reconnect.png"))
                        .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));

        reconnectButton.setPreferredSize(new Dimension(20, 20));
        reconnectButton.setBackground(headerFooterBGColor);
        reconnectButton.setForeground(whitestColor);
        reconnectButton.setBorder(new EmptyBorder(0, 1, 0, 3));
        final JSButtonUI uiReconnect = new JSButtonUI();
        uiReconnect.setPressedColor(reconnectButton.getBackground().darker());
        reconnectButton.setUI(uiReconnect);
        reconnectButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        footer.add(reconnectButton,
                new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1,
                        com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER,
                        com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED,
                        com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null,
                        null,
                        new Dimension(20, 20), 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font getFont(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null)
            return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(),
                size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize())
                : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback
                : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    private class SubmitSwitchUsernameAction extends AbstractAction {
        public SubmitSwitchUsernameAction() {
            super("SwitchUsername");
        }

        public void actionPerformed(ActionEvent action) {
            String usernameAsked = usernameTextField.getText();
            if (usernameAsked.equals(controller.getUsername()))
                return;
            String res = controller.changeUsername(usernameAsked);
            if (res.equals("")) {
                usernameTextField.setText(controller.getUsername());
            } else {
                ErrorPopup.show("Impossible to change username", res);
                return;
            }

            updateGUI();
            Log.l("Switching username to '" + usernameAsked + "'", Log.LOG);
        }
    }

    private class SubmitMessageAction extends AbstractAction {
        public SubmitMessageAction() {
            super("SubmitMessage");
        }

        public void actionPerformed(ActionEvent action) {
            String messageToSend = inputMessage.getText();
            sendMsg(messageToSend.trim());

        }

        public void sendMsg(String messageToSend) {
            if (messageToSend == null || messageToSend.isEmpty() || messageToSend.isBlank()
                    || controller.getConversationName() == null)
                return;
            MainController.sendMessage.safePut(new Message(controller.getUsername(),
                    controller.getConversationName(), messageToSend, controller.getTime()));

            inputMessage.setText("");
            updateGUI();
        }
    }

    private static String displayStatus(Status status) {
        switch (status) {
            case Offline:
                return "<br><span color=\"red\">(offline)</span>";
            case Busy:
                return "<br><span color=\"orange\">(busy)</span>";
            case Away:
                return "<br><span color=\"yellow\">(away)</span>";
            case Online:
                return "";

            default:
                return "";
        }
    }
}
