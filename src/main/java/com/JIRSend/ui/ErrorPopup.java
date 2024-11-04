package com.JIRSend.ui;

import javax.swing.JOptionPane;

public class ErrorPopup {
    public static void show(String title, String message) {
        JOptionPane.showMessageDialog(
            null, 
            message, 
            "Error: " + title, 
            JOptionPane.ERROR_MESSAGE
        );
    }
}
