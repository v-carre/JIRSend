package com.JIRSend.model.network;

import java.net.SocketException;
import java.util.concurrent.Semaphore;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import com.JIRSendApp.model.network.Net;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// @Disabled("Disabled until JUnit is smart!")
@Tag("Net")
@ResourceLock("NETWORK_RESSOURCE")
public class NetTest {
    static Semaphore mutex;
    static Net net;

    @BeforeAll
    static void setup() throws SocketException {
        mutex = new Semaphore(0);
        net = new Net(null, () -> {
            mutex.release();
        }, true);
    }

    @AfterAll
    static void cleanup() throws InterruptedException {
        net.stop();
        Thread.sleep(100);
    }

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

    @Test
    void testPrintHM() {
        assertDoesNotThrow(() -> net.printHashMap());
    }

    @Test
    void testGetIpFromUsername() {
        assertEquals(null, net.getIpFromUsername("doesntexist"));
    }
}
