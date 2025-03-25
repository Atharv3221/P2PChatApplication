package main.java.com.p2pchatapplication;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private String username;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private Scanner scanner;

    public ChatClient(String username, String userId, Scanner scanner) {
        this.username = username;
        this.scanner = scanner;
    }

    public void startClient() {
        try {
            socket = new Socket("localhost", 5000);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send username to server
            writer.println(username);

            // Start listening for incoming messages
            new Thread(this::listenForMessages).start();

            while (true) {
                String message = scanner.nextLine();

                if (message.equalsIgnoreCase("@Users")) {
                    displayUserList();
                } else if (message.startsWith("@Add ")) {
                    addUser(message);
                } else if (message.equalsIgnoreCase("@Exit")) {
                    exitChat();
                    break;
                } else {
                    writer.println(message); // Send normal message
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Received: " + message);
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server.");
        }
    }

    private void displayUserList() {
        System.out.println("Users List:");
        for (String user : UserManager.getUsersFromFile()) {
            System.out.println(user);
        }
    }

    private void addUser(String message) {
        String[] parts = message.split(" ");
        if (parts.length == 3) {
            String newUsername = parts[1];
            String newUserId = parts[2];
            if (UserManager.addUserToFile(newUsername, newUserId)) {
                System.out.println(newUsername + " added successfully!");
            } else {
                System.out.println("User already exists.");
            }
        } else {
            System.out.println("Invalid format. Use: @Add username 8DigitId");
        }
    }

    private void exitChat() {
        try {
            writer.println("@Exit");
            socket.close();
            System.out.println("Chat closed.");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
