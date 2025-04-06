package p2pchatapplication;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class BotManager {
    private static final String botUsername = "Bot1"; // Hardcoded username for the bot
    private Map<String, String> discoveredUsers = new HashMap<>(); // stores discovered usernames and their IPs
    private static final int DISCOVERY_PORT = 6000; // The port that both human clients and bots use

    public BotManager() {
        // Constructor
    }

    /**
     * Starts broadcasting a discovery message to discover other users in the network.
     */
    public void startDiscovery() {
        System.out.println("Bot " + botUsername + " is starting discovery...");

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);

            String message = "DISCOVER " + botUsername;
            byte[] messageBytes = message.getBytes();
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length,
                    InetAddress.getByName("255.255.255.255"), DISCOVERY_PORT); // Broadcasting on port 8888
            socket.send(packet);

            System.out.println("Discovery message sent: " + message);
        } catch (IOException e) {
            System.out.println("Error during discovery: " + e.getMessage());
        }
    }

    /**
     * Starts listening for responses from other users/bots.
     * It will listen for a 'sendOurIp' message to respond with the bot's IP.
     */
    public void startResponseListener() {
        System.out.println("Bot " + botUsername + " is listening for responses on port " + DISCOVERY_PORT + "...");

        try (DatagramSocket socket = new DatagramSocket(DISCOVERY_PORT)) {
            byte[] buffer = new byte[256];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received message: " + receivedMessage);

                if (receivedMessage.startsWith("DISCOVER") && !receivedMessage.contains(botUsername)) {
                    String targetUsername = receivedMessage.split(" ")[1];
                    System.out.println("Bot " + botUsername + " discovered user: " + targetUsername);

                    // Save the discovered user IP and send a response with the bot's IP
                    discoveredUsers.put(targetUsername, packet.getAddress().getHostAddress());
                    sendIp(packet.getAddress(), packet.getPort(), targetUsername);
                }
            }
        } catch (IOException e) {
            System.out.println("Error during response listening: " + e.getMessage());
        }
    }

    /**
     * Sends the bot's IP back to a discovered user who requested it.
     */
    private void sendIp(InetAddress targetAddress, int targetPort, String targetUsername) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String responseMessage = "sendOurIp " + botUsername + " " + targetUsername;
            byte[] responseBytes = responseMessage.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, targetAddress, targetPort);
            socket.send(responsePacket);

            System.out.println("Bot " + botUsername + " sent IP response to " + targetUsername);
        } catch (IOException e) {
            System.out.println("Error sending IP response: " + e.getMessage());
        }
    }

    /**
     * Bot sends a message to a known user.
     */
    public void sendMessageToUser(String targetUsername, String message) {
        String targetIp = discoveredUsers.get(targetUsername);
        if (targetIp != null) {
            try (DatagramSocket socket = new DatagramSocket()) {
                byte[] messageBytes = message.getBytes();
                InetAddress targetAddress = InetAddress.getByName(targetIp);
                DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, targetAddress, DISCOVERY_PORT);
                socket.send(packet);

                System.out.println("Bot " + botUsername + " sent message to " + targetUsername + ": " + message);
            } catch (IOException e) {
                System.out.println("Error sending message: " + e.getMessage());
            }
        } else {
            System.out.println("Bot " + botUsername + " could not find " + targetUsername + " on the network.");
        }
    }

    /**
     * Main method to start the bot and initiate discovery and response listening.
     */
    public static void main(String[] args) {
        BotManager botManager = new BotManager();

        // Start discovery and response listeners for the bot
        botManager.startDiscovery();
        botManager.startResponseListener();
    }
}
