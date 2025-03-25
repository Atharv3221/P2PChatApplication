package main.java.com.p2pchatapplication;


import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;
    private BufferedReader reader;
    private String username;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            username = reader.readLine();
            System.out.println(username + " has joined.");

            String message;
            while ((message = reader.readLine()) != null) {
                if (message.equalsIgnoreCase("@Exit")) {
                    System.out.println(username + " has left the chat.");
                    break; // Exit the loop when @Exit is received
                } else if (message.equalsIgnoreCase("@Users")) {
                    sendUserList();
                } else {
                    broadcastMessage("Received: @" + username + " " + message);
                }
            }
        } catch (IOException e) {
            System.out.println(username + " disconnected.");
        } finally {
            closeConnection();
        }
    }

    private void sendUserList() {
        StringBuilder userList = new StringBuilder("Online users: ");
        for (ClientHandler client : server.getClients()) {
            userList.append(client.username).append(", ");
        }
        writer.println(userList.toString());
    }

    private void broadcastMessage(String message) {
        for (ClientHandler client : server.getClients()) {
            if (!client.equals(this)) {
                client.writer.println(message);
            }
        }
    }

    public void closeConnection() {
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
            server.getClients().remove(this); // Remove client from the list
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
