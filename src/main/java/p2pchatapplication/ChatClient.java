package p2pchatapplication;

import java.io.*;
import java.net.*;

public class ChatClient {
    public static void sendMessage(String from, String to, String message, int port) {
        String ip = FriendManager.getFriendIP(to);
        if (ip == null) {
            System.out.println("❌ @" + to + " is not online or not added.");
            return;
        }

        try (Socket socket = new Socket(ip, port)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println("{\"type\":\"MESSAGE\",\"from\":\"" + from + "\",\"message\":\"" + message + "\"}");
            System.out.println("Sent: @" + to + " \"" + message + "\"");
            appendToChatHistory("Sent: @" + to + " \"" + message + "\"");
        } catch (IOException e) {
            System.out.println("❌ Failed to send: " + e.getMessage());
        }
    }

    public static void appendToChatHistory(String message) {
        try (FileWriter fw = new FileWriter("src/main/resources/chat_history.txt", true)) {
            fw.write(message + "\n");
        } catch (IOException ignored) {}
    }
}
