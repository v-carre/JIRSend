package com.JIRSendMod.network.packets;

/**
 * Specify the type of each message
 * Main way to distinguish the behaviour expected
 */
public enum MessageType {
    Base_Message("Base_Message"),
    Discover_Users("Discover_Users"),
    Answer_Discovery("Answer_Discovery"),
    New_User("New_User"),
    Notify_Presence("Notify_Presence"),
    Change_Name("Change_Name"),
    Disconnect("Disconnect");

    private final String description;

    // Constructor
    MessageType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
