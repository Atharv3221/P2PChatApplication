package com.chatapp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("ChatServer started successfully");
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    new ClientHandler(clientSocket).start();
                }
            } catch (IOException e) {
                System.out.println("Error starting server: " + e.getMessage());
            }
        }).start();
    }

    private static class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter output = new PrintWriter(socket.getOutputStream(), true)) {

                String message;
                while ((message = input.readLine()) != null) {
                    System.out.println("Received: " + message);
                    output.println("Echo: " + message);
                }
            } catch (IOException e) {
                System.out.println("Client disconnected");
            }
        }
    }
}
