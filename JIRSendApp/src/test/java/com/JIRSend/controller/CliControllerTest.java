package com.JIRSend.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.SocketException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import com.JIRSendApp.controller.MainController;
import com.JIRSendApp.model.network.Net;

import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

@Tag("Controller")
@ResourceLock("NETWORK_RESSOURCE")
public class CliControllerTest {
    static MainController controller;
    static File hdb = new File("./history.db");
    static File hdbTemp = new File("./temphistory.db");
    static boolean hdbExists = false;
    public static final boolean isWindowsSystem = System.getProperty("os.name").contains("Windows");
    
    @BeforeAll
    @DisabledOnOs(OS.WINDOWS)
    static void setup() throws SocketException {
        if (isWindowsSystem) return;
        if (hdb.exists()) {
            hdbExists = true;
            hdb.renameTo(hdbTemp);
        }
        controller = new MainController(false, true);
    }

    @AfterAll
    @DisabledOnOs(OS.WINDOWS)
    static void cleanup() throws InterruptedException {
        if (isWindowsSystem) return;
        controller.stopNet();
        Thread.sleep(100);
        if (hdbExists && hdbTemp.exists()) {
            hdbTemp.renameTo(hdb);
        }
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testChangeUsername() {
        assertEquals(Net.okString, controller.changeUsername("ShouldBeOK"));
        assertEquals("ShouldBeOK", controller.getUsername());
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testGetConnectedUsernames() throws SocketException {
        assertTrue(controller.getConnectedUsernames().isEmpty());
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testGetContacts() throws SocketException {
        assertTrue(controller.getContacts().isEmpty());
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testGetConversation() {
        assertEquals(null, controller.getConversation());
        assertEquals(null, controller.getConversation("doesntexist"));
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testGetConversationIP() {
        assertEquals(null, controller.getConversationIP());
        assertEquals(null, controller.getConversationIP("doesntexist"));
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testGetConversationName() {
        assertEquals(null, controller.getConversationName());
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testGetConversationUnreadNumber() {
        assertEquals(0, controller.getConversationUnreadNumber("doesntexist"));
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testGetIPFromUsername() {
        assertEquals(null, controller.getIPFromUsername("doesntexist"));
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testGetName() {
        assertEquals("JIRSend Main", controller.getName());
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testGetNumberConnected() {
        assertEquals(0, controller.getNumberConnected());
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testGetTotalUnread() {
        assertEquals(0, controller.getTotalUnread());
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testGetUsernameFromIP() {
        assertEquals(null, controller.getUsernameFromIP("noIP"));
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testIsConnected() {
        assertFalse(controller.isConnected("doesntexist"));
    }

    @Test
    @DisabledOnOs(OS.WINDOWS)
    void testMarkConversationRead() {
        assertDoesNotThrow(() -> controller.markConversationRead("doesntexist"));
    }

    // only run on Windows
    @Test
    @EnabledOnOs(OS.WINDOWS)
    void testOnlyOnWindows() {
        System.out.println("Lol, windows.");
    }
}
