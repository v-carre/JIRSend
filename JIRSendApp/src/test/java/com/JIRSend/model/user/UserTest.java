package com.JIRSend.model.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.JIRSendApp.model.Message;
import com.JIRSendApp.model.user.BaseUser;
import com.JIRSendApp.model.user.Conversation;
import com.JIRSendApp.model.user.User;
public class UserTest {

    @Test
    void testAddToConversation() {
        BaseUser user = new User(null, "username");
        user.addToConversation("127.0.0.1", new Message("me", "other", "msg"));
        Conversation conv = user.getConversation("127.0.0.1");
        assertEquals(1, conv.getMessages().size());
        assertEquals("me", conv.getMessages().get(0).sender);
        assertEquals("other", conv.getMessages().get(0).receiver);
        assertEquals("msg", conv.getMessages().get(0).message);
    }

    @Test
    void testGetConversation() {
        BaseUser user = new User(null, "username");
        Conversation conv = user.getConversation("null");
        assertTrue(conv.getMessages().isEmpty());
    }

    @Test
    void testGetConversationUnreadNb() {
        BaseUser user = new User(null, "user");
        assertEquals(0, user.getConversationUnreadNb("127.0.0.1"));
        for (int i=1;i<5;++i) {
            user.addToConversation("127.0.0.1", null);
            assertEquals(i, user.getConversationUnreadNb("127.0.0.1"));
        }
    }

    @Test
    void testGetTotalUnread() {
        BaseUser user = new User(null, "user");
        assertEquals(0, user.getTotalUnread());
        user.addToConversation("127.0.0.1", null);
        user.addToConversation("127.0.0.2", null);
        user.addToConversation("127.0.0.3", null);
        user.addToConversation("127.0.0.4", null);
        user.addToConversation("127.0.0.5", null);
        assertEquals(5, user.getTotalUnread());
    }
}
