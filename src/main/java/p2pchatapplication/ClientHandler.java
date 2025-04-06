package p2pchatapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Handles an incoming connection to the ChatServer.
 */
public class ClientHandler implements Runnable {
    private final Socket clientSocket;

    /**
     * Constructs a new ClientHandler.
     *
     * @param socket the socket connected to the client
     */
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    /**
     * Continuously reads and processes incoming messages from the socket.
     */
    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("@")) {
                    int sepIndex = line.indexOf('|');
                    if (sepIndex != -1) {
                        String sender = line.substring(1, sepIndex);
                        String message = line.substring(sepIndex + 1);
                        ChatServer.printReceivedMessage(sender, message);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[ClientHandler] Connection closed or error: " + e.getMessage());
        }
    }
}
