package main.java.com.p2pchatapplication;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 5000;
    private static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private ServerSocket serverSocket;

    public void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Chat server started...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.out.println("Server shutting down...");
        }
    }

    public List<ClientHandler> getClients() {
        return clients;
    }

    public void stopServer() {
        try {
            for (ClientHandler client : clients) {
                client.closeConnection(); // Now this method exists!
            }
            serverSocket.close();
            System.out.println("Server stopped.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
}
