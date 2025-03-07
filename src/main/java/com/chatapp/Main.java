// Main.java - Entry point to start Chat Server

package com.chatapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.util.*;
import java.time.LocalDateTime;

public class Main {
    private static final String USER_FILE = "users.json";
    private static final String DATA_DIR = "data";
    private static Map<String, String> users = new HashMap<>();
    public static String currentUser;
    public static String currentUserId;
    private static Map<String, Boolean> storagePreferences = new HashMap<>();

    public static void main(String[] args) {
        loadUsers();
        registerUserIfNeeded();
        ensureDataDirectoryExists();
        int port = 8080;
        ChatServer server = new ChatServer(port);
        server.start();
        System.out.println("P2P Chat Application Started");
        System.out.println("Chat Server running on port: " + port);
    }

    private static void loadUsers() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(USER_FILE);
        if (file.exists()) {
            try {
                users = mapper.readValue(file, Map.class);
            } catch (IOException e) {
                System.out.println("Failed to load users: " + e.getMessage());
            }
        }
    }

    private static void registerUserIfNeeded() {
        File identityFile = new File("identity.txt");
        if (identityFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(identityFile))) {
                currentUser = reader.readLine();
                currentUserId = users.get(currentUser);
                System.out.println("Welcome back, " + currentUser + "!");
                return;
            } catch (IOException e) {
                System.out.println("Error reading identity file: " + e.getMessage());
            }
        }
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        currentUser = scanner.nextLine().trim();
        
        if (!users.containsKey(currentUser)) {
            currentUserId = UUID.randomUUID().toString();
            users.put(currentUser, currentUserId);
            saveUsers();
        } else {
            currentUserId = users.get(currentUser);
        }
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(identityFile))) {
            writer.write(currentUser);
        } catch (IOException e) {
            System.out.println("Error saving identity file: " + e.getMessage());
        }
        
        System.out.println("Welcome, " + currentUser + "! Your unique ID: " + currentUserId);
    }

    private static void saveUsers() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(USER_FILE), users);
        } catch (IOException e) {
            System.out.println("Failed to save users: " + e.getMessage());
        }
    }

    private static void ensureDataDirectoryExists() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
    }
}