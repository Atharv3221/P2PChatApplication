package p2pchatapplication;

import java.util.Scanner;

/**
 * Entry point for the P2P Chat Application.
 * Initializes the user, starts the server, client, and friend discovery listeners.
 * Provides a CLI for sending messages and managing friends.
 */
public class Main {
    public static void main(String[] args) {
        // Step 1: Initialize user credentials
        UserManager userManager = new UserManager();
        userManager.initializeCredential();

        // Step 2: Start the server thread to listen for incoming messages
        Thread serverThread = new Thread(new ChatServer(), "ChatServer-Thread");
        serverThread.start();

        // Optional delay to allow server to initialize properly
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("[Main] Server initialization interrupted.");
        }

        // Step 3: Start the chat client to send messages
        ChatClient chatClient = new ChatClient();

        // Step 4: Start discovery listeners (DISCOVER and RESPONSE handlers)
        FriendManager friendManager = FriendManager.getInstance();
        String myUsername = UserManager.getMyUsername();
        friendManager.startDiscoveryListener(myUsername);
        friendManager.startResponseListener();
        // Discover all known users on LAN
        friendManager.broadcastDiscoveryToAll();  // <-- Add this line

        // Step 5: Start command-line interface for user input
        Scanner scanner = new Scanner(System.in);
        CommandHandler commandHandler = new CommandHandler(chatClient, friendManager);

        System.out.println("Welcome, @" + myUsername + "!");
        System.out.println("You can now chat! Type @Users to see who’s online, @Add <username> to add a friend, or @Exit to quit.");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("@Exit")) {
                System.out.println("[Main] Exiting application...");
                System.exit(0);
            }

            commandHandler.handleCommand(input);

            // Brief delay to prevent flooding console output
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}
        }

        // Main thread ends; background threads continue unless externally terminated
    }
}
