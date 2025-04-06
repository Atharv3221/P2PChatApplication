package p2pchatapplication;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Sends multiple test messages to the chat server with delays.
 */
public class TestMessageSender {
    public static void main(String[] args) {
        String serverIp = "127.0.0.1"; // localhost
        int port = 5000; // make sure this matches your ChatServer PORT

        try (Socket socket = new Socket(serverIp, port);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            String username = "TestMethod";

            String[] messages = {
                "Hello from test!",
                "How are you?",
                "Just testing the server.",
                "Final message. Bye!"
            };

            for (String message : messages) {
                String fullMessage = "@" + username + "|" + message;
                writer.println(fullMessage);
                System.out.println("[TestMessageSender] Sent: " + fullMessage);

                Thread.sleep(2000); // Wait for 2 seconds
            }

        } catch (Exception e) {
            System.out.println("[TestMessageSender] Error: " + e.getMessage());
        }
    }
}
