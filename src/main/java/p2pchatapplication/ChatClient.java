package p2pchatapplication;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Responsible for sending peer-to-peer messages to a recipient over LAN.
 */
public class ChatClient {
    private static final int PORT = 5000;

    /**
     * Sends a formatted message to the given IP address.
     *
     * Format: "@senderUsername|message"
     *
     * @param targetUsername   The recipient's username (for display only)
     * @param message          The message content
     * @param recipientIp      The IP address of the recipient
     * @param senderUsername   The sender's username
     */
    public void sendMessage(String targetUsername, String message, String recipientIp, String senderUsername) {
        try (Socket socket = new Socket(recipientIp, PORT);
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            String formattedMessage = "@" + senderUsername + "|" + message;
            writer.println(formattedMessage);

            System.out.println("Sent: @" + targetUsername + " \"" + message + "\"");

        } catch (Exception e) {
            System.out.println("[ChatClient] Failed to send message to @" + targetUsername + ": " + e.getMessage());
        }
    }
}
