package p2pchatapplication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The ChatServer class listens for incoming messages on a socket.
 * It spawns a new ClientHandler thread for each connection.
 */
public class ChatServer implements Runnable {
    private static final int PORT = 5000; // You can change this if needed

    /**
     * Starts the server socket to accept incoming messages.
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[ChatServer] Listening on port " + PORT + "...");

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                   new Thread(new ClientHandler(clientSocket)).start();
                } catch (IOException e) {
                    System.out.println("[ChatServer] Failed to accept client: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.out.println("[ChatServer] Error starting server: " + e.getMessage());
        }
    }

    /**
     * Prints received messages in the required format.
     *
     * @param username the sender's username
     * @param message  the message text
     */
    public static void printReceivedMessage(String username, String message) {
        System.out.println("Received: @" + username + " \"" + message + "\"");
    }
}
