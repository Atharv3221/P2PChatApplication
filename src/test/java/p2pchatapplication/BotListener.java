package p2pchatapplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

/**
 * A lightweight bot listener that simulates a bot receiving messages and supports discovery.
 */
public class BotListener {

    private static final int TCP_PORT = 6000;
    private static final int DISCOVERY_PORT = 6000;
    private static final int RESPONSE_PORT = 6001;

    private static final String BOT_USERNAME = "Bot1"; // You can run multiple bots with different usernames

    public static void main(String[] args) {
        System.out.println("[BotListener] Bot @" + BOT_USERNAME + " is listening...");

        // Start TCP message listener
        new Thread(BotListener::startTcpListener).start();

        // Start UDP discovery listener
        new Thread(BotListener::startDiscoveryListener).start();
    }

    /**
     * Listens for direct TCP messages (normal chat).
     */
    private static void startTcpListener() {
        try (ServerSocket serverSocket = new ServerSocket(TCP_PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();

                new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("@")) {
                                int sepIndex = line.indexOf('|');
                                if (sepIndex != -1) {
                                    String sender = line.substring(1, sepIndex);
                                    String message = line.substring(sepIndex + 1);
                                    System.out.println("Received: @" + sender + " \"" + message + "\"");
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("[BotListener] Error in TCP handler: " + e.getMessage());
                    }
                }).start();
            }
        } catch (Exception e) {
            System.out.println("[BotListener] TCP listener error: " + e.getMessage());
        }
    }

    /**
     * Listens for DISCOVER messages on UDP port 6000 and responds if it's meant for this bot.
     */
    private static void startDiscoveryListener() {
        try (DatagramSocket socket = new DatagramSocket(DISCOVERY_PORT, InetAddress.getByName("0.0.0.0"))) {
            socket.setBroadcast(true);
            byte[] buffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());

                if (received.startsWith("DISCOVER|")) {
                    String[] parts = received.split("\\|");
                    if (parts.length == 3) {
                        String sender = parts[1];
                        String target = parts[2];
                        String senderIp = packet.getAddress().getHostAddress();

                        if (target.equals(BOT_USERNAME)) {
                            // Send RESPONSE back to sender
                            String response = "RESPONSE|" + BOT_USERNAME;
                            byte[] responseData = response.getBytes();
                            DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length,
                                    InetAddress.getByName(senderIp), RESPONSE_PORT);
                            new DatagramSocket().send(responsePacket);

                            System.out.println("[BotListener] Sent RESPONSE to @" + sender);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[BotListener] Discovery listener error: " + e.getMessage());
        }
    }
}
