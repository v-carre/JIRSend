package com.JIRSendMod.model;

/*
 * User availability
 *
 * Holds no logical value and is displayed on user profiles.
 */
public enum UserStatus {
    /**
     * No availability information was provided
     */
    UNKNOWN("UNKNOWN"),
    /**
     * User is idling, not doing anything
     */
    IDLE("IDLE"),
    /**
     * User is available for conversation
     */
    AVAILABLE("AVAILABLE"),
    /**
     * User is marked as busy and does not want to be interrupted
     */
    DO_NOT_DISTURB("DO_NOT_DISTURB");

    private final String description;

    // Constructor
    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static UserStatus fromDescription(String description) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getDescription().equalsIgnoreCase(description)) {
                return status;
            }
        }
        return UNKNOWN;
    }
}
