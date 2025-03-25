package main.java.com.p2pchatapplication;


import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String[] credentials = UserManager.getUserCredentials();
        String username;
        String userId;

        if (credentials == null) {
            System.out.print("Enter your username: ");
            username = scanner.nextLine();
            userId = UserManager.generateRandomId();
            UserManager.saveUserCredentials(username, userId);
        } else {
            username = credentials[0];
            userId = credentials[1];
        }

        System.out.println("Welcome, " + username + " (" + userId + ")!");

        // Start the server in a separate thread
        new Thread(() -> {
            ChatServer server = new ChatServer();
            server.startServer();
        }).start();

        // Start the client and pass the scanner
        ChatClient client = new ChatClient(username, userId, scanner);
        client.startClient();
    }
}
