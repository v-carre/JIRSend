package com.gestionProjet.network;

public class Net {
    public Net() {
    }

    public boolean usernameAvailable(String username) {
        return !username.isEmpty();
    }
}
