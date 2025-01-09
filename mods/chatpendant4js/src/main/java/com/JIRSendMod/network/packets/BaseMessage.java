package com.JIRSendMod.network.packets;

import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;

public record BaseMessage(
    MessageType messageType,
    InetAddress ipSource,
    String usernameSource,
    Date dateSending
) {
    /**
     * Convert packet to JSON object, for sending
     *
     * Source IP should not be included, as it will be present in IP header.
     * May be overridden by children to add message-specific fields
     * @return
     */
    public JSONObject toJson() {
        return new JSONObject(getBaseJsonFields());
    }

    /**
     * Initialize fields present in every packet
     * @return
     */
    protected Map<String, String> getBaseJsonFields() {
        Map<String, String> map = new HashMap<String, String>();

        map.put("Type", this.messageType().getDescription());
        map.put("usernameSource", this.usernameSource());
        map.put("dateSending", this.dateSending().getTime() + "");
        return map;
    }
}
