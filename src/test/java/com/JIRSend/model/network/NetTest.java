package com.JIRSend.model.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.Semaphore;

import org.junit.jupiter.api.Test;

public class NetTest {
    Semaphore mutex = new Semaphore(0);
    Net net = new Net(null, () -> {
        mutex.release();
    });

    @Test
    void testNet() {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            assertTrue(false);
            return;
        }
        assertTrue(net.getHashMap().isEmpty());
        assertTrue(net.getUserEntries().isEmpty());
        assertEquals(Net.okString, net.usernameAvailable("any_username"));
        assertNotEquals(Net.okString, net.usernameAvailable("incorrect username")); // with space
        assertNotEquals(Net.okString, net.usernameAvailable("incorrect:username")); // with ':'
        assertNotEquals(Net.okString, net.usernameAvailable("!"));                  // too short
        assertNotEquals(Net.okString, net.usernameAvailable("TooLongUsername...")); // too long
        
    }
}
