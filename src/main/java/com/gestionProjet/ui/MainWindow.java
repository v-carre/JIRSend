package com.gestionProjet.ui;

import com.gestionProjet.users.Admin;
import com.gestionProjet.users.BaseUser;
import com.gestionProjet.users.User;
import com.gestionProjet.users.Volunteer;

import javax.swing.*;

public class MainWindow {
    private enum State {
        notInit, waitConnection, personal
    }

    protected BaseUser user;
    private State state;
    private JFrame frame;
    private GUISection currentSection;
    private JPanel currentPanel;

    public MainWindow() {
        this.state = State.notInit;
        this.currentSection = new GUISectionConnection(this, frame);
    }

    public void open() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createWindow();
            }
        });
    }

    private void createWindow() {
        frame = new JFrame("A.C.L.V.");// Aide Centralisée pour La Vie
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        switchToNextSection();
        // frame.remove(frame);
        // frame.add(currentSection.createPanel());

        frame.pack();
        frame.setVisible(true);
    }

    protected void switchToNextSection() {
        if (state != State.notInit)
            frame.remove(currentPanel);

        if (state == State.notInit) {
            state = State.waitConnection;
            currentSection = new GUISectionConnection(this, frame);
        } else if (state == State.waitConnection) {
            state = State.personal;
            currentSection = new GUISectionPersonalInfo(this, frame);
        }

        System.out.println(currentSection.getSectionName());
        currentPanel = currentSection.createPanel();
        frame.add(currentPanel);
        frame.revalidate();
        frame.repaint();
    }

    protected void setUser(String firstName, String lastName, String userType) {
        switch (userType) {
            case "User" -> this.user = new User(firstName, lastName);
            case "Volontaire" -> this.user = new Volunteer(firstName, lastName);
            case "El Decidor" -> this.user = new Admin(firstName, lastName);
            default -> {
                return;
            }
        }
    }
}
