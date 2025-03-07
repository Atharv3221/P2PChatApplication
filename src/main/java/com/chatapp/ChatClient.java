// ChatClient.java - Connects to Chat Server

package com.chatapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.*;

public class ChatClient extends WebSocketClient {
    public ChatClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to server");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("Error: " + ex.getMessage());
    }

    public void sendMessage(String recipientId, String messageText) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> message = new HashMap<>();
            message.put("sender", Main.currentUserId);
            message.put("recipient", recipientId);
            message.put("message", messageText);
            send(mapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }
}
