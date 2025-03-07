// ChatServer.java - Handles multiple client connections

package com.chatapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.WebSocket;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.*;

public class ChatServer extends WebSocketServer {
    private static Map<WebSocket, String> connections = new HashMap<>();

    public ChatServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        connections.put(conn, null);
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> msgData = mapper.readValue(message, Map.class);
            String sender = msgData.get("sender");
            String recipient = msgData.get("recipient");
            String msgText = msgData.get("message");

            if (sender != null) {
                connections.put(conn, sender);
            }

            if (recipient != null && connections.containsValue(recipient)) {
                for (Map.Entry<WebSocket, String> entry : connections.entrySet()) {
                    if (entry.getValue().equals(recipient)) {
                        entry.getKey().send(mapper.writeValueAsString(msgData));
                        break;
                    }
                }
            } else {
                conn.send("User not available.");
            }
        } catch (IOException e) {
            System.out.println("Message handling error: " + e.getMessage());
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        connections.remove(conn);
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("Error: " + ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("ChatServer started successfully");
    }
}
