package com.JIRSend.model.user;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.JIRSend.model.Message;

public class UserTest {
    
    @Test
    void testAddToConversation() {
        BaseUser user = new User(null,"username");
        user.addToConversation("127.0.0.1", new Message("me","other","msg"));
        Conversation conv = user.getConversation("127.0.0.1");
        assertEquals(1, conv.getMessages().size());
        assertEquals("me", conv.getMessages().get(0).sender);
        assertEquals("other", conv.getMessages().get(0).receiver);
        assertEquals("msg", conv.getMessages().get(0).message);
    }

    @Test
    void testGetConversation() {
        BaseUser user = new User(null,"username");
        Conversation conv = user.getConversation("null");
    }

    @Test
    void testGetConversationUnreadNb() {

    }

    @Test
    void testGetCurrentConversationName() {

    }

    @Test
    void testGetTotalUnread() {

    }
}
