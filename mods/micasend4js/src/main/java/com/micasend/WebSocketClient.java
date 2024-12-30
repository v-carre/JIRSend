package com.micasend;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.ContainerProvider;
// import jakarta.websocket.ContainerProvider;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
// import org.glassfish.tyrus.client.ClientManager;

import java.net.URI;

import com.micasend.MicaSend4JS.VoidCallback;

@ClientEndpoint
public class WebSocketClient {
    private Session session;
    private final VoidCallback callback;
    private final String uri;

    public WebSocketClient(String uri, VoidCallback callback) {
        this.callback = callback;
        this.uri = uri;
        connect();
    }

    private void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            // WebSocketContainer container = ClientManager.createClient();
            container.connectToServer(this, new URI(uri));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        // System.out.println("Connected to WebSocket server");
    }

    @OnMessage
    public void onMessage(String message) {
        // System.out.println("Received message: " + message);
        callback.execute();
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        // System.out.println("Connection closed: " + closeReason.getReasonPhrase());
        // System.out.println("REFRESHING WEBSOCKET CONNECTION"); 
        // delay before reconnect
        try {
            Thread.sleep(1000);
            connect();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    public void sendMessage(String message) {
        try {
            session.getBasicRemote().sendText(message);
            System.out.println("Pinged");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
