package p2pchatapplication;

import java.io.*;
import java.net.*;

public class ChatServer {
    private final int port;

    public ChatServer(int port) { this.port = port; }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}
