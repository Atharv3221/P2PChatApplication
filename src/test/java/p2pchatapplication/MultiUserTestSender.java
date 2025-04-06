package p2pchatapplication;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Simulates multiple users sending messages to the ChatServer.
 */
public class MultiUserTestSender {

    public static void main(String[] args) {
        String serverIp = "127.0.0.1";
        int port = 5000; // Match your ChatServer port

        String[] users = { "User1", "User2", "User3" };
        String[] messages = {
            "Hello from User1!",
            "User2 reporting in!",
            "Hey, it's User3!"
        };

        for (int i = 0; i < users.length; i++) {
            final String user = users[i];
            final String msg = messages[i];

            // Use a separate thread for each user
            new Thread(() -> {
                try {
                    // Sleep before sending to stagger them
                    Thread.sleep((int) (Math.random() * 2000));

                    try (Socket socket = new Socket(serverIp, port);
                         PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

                        writer.println("@" + user + "|" + msg);
                        System.out.println("[" + user + "] Message sent!");

                    }
                } catch (Exception e) {
                    System.out.println("[" + user + "] Error: " + e.getMessage());
                }
            }).start();
        }
    }
}
