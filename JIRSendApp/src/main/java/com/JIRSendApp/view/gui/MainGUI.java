package com.JIRSendApp.view.gui;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import com.JIRSendApp.controller.MainController;
import com.JIRSendApp.view.MainAbstractView;
import com.JIRSendApp.view.cli.Log;

public class MainGUI extends MainAbstractView {
    private enum State {
        notInit, loadScreen, waitConnection, chat, personal
    }

    protected MainController controller;

    private boolean noPanel;

    protected String lastError;

    private State state;
    private JFrame frame;
    private GUISection currentSection;
    private JPanel currentPanel;

    public MainGUI(MainController controller) {
        this.controller = controller;
        this.state = State.notInit;
        this.noPanel = true;
        this.currentSection = new GUISectionConnection(this, frame);
        this.lastError = "";
    }

    @Override
    public void start() throws HeadlessException {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Log.l("Starting window", Log.LOG);
                createWindow();
            }
        });
    }

    @Override
    public void open() {
        state = State.waitConnection;
        refreshSection();
    }

    private void createWindow() {
        frame = new JFrame("JIRSend");
        frame.setTitle("JIRSend");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                controller.stoppingApp();
            }
        });

        ImageIcon img = new ImageIcon("assets/jirsend_logo.png");

        switchToNextSection();

        frame.pack();
        frame.setVisible(true);
        frame.setIconImage(img.getImage());
        frame.setSize(400, 500);
    }

    public void updateIcon() {
        ImageIcon img;
        int totalUnread = controller.getTotalUnread();
        if (totalUnread > 0) {
            img = new ImageIcon("assets/jirsend_logo_notif.png");
            frame.setName("JIRSend (" + totalUnread + ")");
            frame.setTitle("JIRSend (" + totalUnread + ")");
        } else {
            img = new ImageIcon("assets/jirsend_logo.png");
            frame.setName("JIRSend");
            frame.setTitle("JIRSend");
        }
        frame.setIconImage(img.getImage());
    }

    protected void switchToNextSection() {
        if (state == State.notInit)
            state = State.loadScreen;
        else if (state == State.loadScreen)
            state = State.waitConnection;
        else if (state == State.waitConnection)
            state = State.chat;
            // we should not arrive in that case, for test purpose only
        else
            state = State.personal;
        refreshSection();
    }

    protected void refreshSection() {
        if (state == State.notInit) {
            return;
        } else if (!noPanel) {
            frame.remove(currentPanel);
        }

        if (state == State.loadScreen)
            currentSection = new GUISectionLoading(this, frame);
        else if (state == State.waitConnection)
            currentSection = new GUISectionConnection(this, frame);
        else if (state == State.personal)
            currentSection = new GUISectionPersonalInfo(this, frame);
        else if (state == State.chat) {
            frame.setSize(800, 400);
            frame.setMinimumSize(new Dimension(800, 400));
            currentSection = new GUISectionMain(this, frame);
        }

        noPanel = false;
        Log.l("New window selection: " + currentSection.getSectionName(), Log.LOG);
        currentPanel = currentSection.createPanel();
        frame.add(currentPanel);
        frame.revalidate();
        frame.repaint();
    }

    protected void refreshFrame() {
        frame.revalidate();
        frame.repaint();
    }
}
