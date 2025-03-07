package com.chatapp;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class ChatClient {
    private WebSocketClient client;
    private String username;

    public ChatClient(String serverUri, String username) {
        this.username = username;
        try {
            client = new WebSocketClient(new URI(serverUri)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    System.out.println("Connected to server.");
                    client.send(username + " joined the chat.");
                }

                @Override
                public void onMessage(String message) {
                    System.out.println(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Disconnected from server: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            };
            client.connect();
        } catch (URISyntaxException e) {
            System.out.println("Invalid server URI: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        if (client != null && client.isOpen()) {
            client.send(username + ": " + message);
        } else {
            System.out.println("Not connected to server.");
        }
    }

    public void closeConnection() {
        if (client != null) {
            client.close();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter server address (ws://localhost:8080): ");
        String serverAddress = scanner.nextLine().trim();
        System.out.print("Enter your username: ");
        String username = scanner.nextLine().trim();

        ChatClient chatClient = new ChatClient(serverAddress, username);

        System.out.println("Type messages to send. Type 'exit' to quit.");
        while (true) {
            String message = scanner.nextLine();
            if ("exit".equalsIgnoreCase(message)) {
                chatClient.closeConnection();
                break;
            }
            chatClient.sendMessage(message);
        }
        scanner.close();
    }
}