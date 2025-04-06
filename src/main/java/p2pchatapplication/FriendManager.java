package p2pchatapplication;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;
import java.util.*;

/**
 * Handles adding friends, discovering them on LAN via UDP, and maintaining
 * an in-memory map of online friends and their IP addresses.
 */
public class FriendManager {
    private static final String USERS_FILE = System.getProperty("user.dir") + File.separator +
            "src" + File.separator + "main" + File.separator + "resources" + File.separator + "users.json";

    private static final int DISCOVERY_PORT = 6000;
    private static final int RESPONSE_PORT = 6001;

    private static final Map<String, String> onlineFriends = new HashMap<>();
    private static final Gson gson = new Gson();
    private static final FriendManager instance = new FriendManager();

    private FriendManager() {}

    /**
     * Gets the singleton instance.
     */
    public static FriendManager getInstance() {
        return instance;
    }

    /**
     * Adds a friend to users.json if not already present.
     */
    public boolean addFriend(String username) {
        List<String> users = getFriends();
        if (!users.contains(username)) {
            users.add(username);
            saveFriends(users);
            return true;
        }
        return false;
    }

    /**
     * Retrieves the list of friend usernames from users.json.
     */
    public List<String> getFriends() {
        try (Reader reader = new FileReader(USERS_FILE)) {
            Type listType = new TypeToken<List<String>>() {}.getType();
            List<String> users = gson.fromJson(reader, listType);
            return users != null ? users : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Saves the given list of friends to users.json.
     */
    private void saveFriends(List<String> friends) {
        try (Writer writer = new FileWriter(USERS_FILE)) {
            gson.toJson(friends, writer);
        } catch (IOException e) {
            System.out.println("[FriendManager] Failed to save friends.");
        }
    }

    /**
     * Returns the IP of a user if they are currently online.
     */
    public String getIpOfUser(String username) {
        return onlineFriends.get(username);
    }

    /**
     * Returns the current map of online friends and their IPs.
     */
    public Map<String, String> getOnlineUsers() {
        return onlineFriends;
    }

    /**
     * Broadcasts DISCOVER messages for all known friends.
     */
    public void broadcastDiscoveryToAll() {
        List<String> friends = getFriends();
        for (String friend : friends) {
            broadcastDiscovery(friend);
        }
    }

    /**
     * Broadcasts a DISCOVER message for a specific friend.
     */
    public void broadcastDiscovery(String targetUsername) {
        String myUsername = UserManager.getMyUsername();
        String message = "DISCOVER|" + myUsername + "|" + targetUsername;

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            byte[] buffer = message.getBytes();

            List<InetAddress> broadcastAddresses = getBroadcastAddresses();

            for (InetAddress broadcast : broadcastAddresses) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcast, DISCOVERY_PORT);
                socket.send(packet);
                System.out.println("[FriendManager] Sent DISCOVER to " + targetUsername + " via " + broadcast.getHostAddress());
            }
        } catch (IOException e) {
            System.out.println("[FriendManager] Failed to broadcast DISCOVER: " + e.getMessage());
        }
    }

    /**
     * Starts listening for incoming DISCOVER packets.
     * Replies with our IP if the message targets us.
     */
    public void startDiscoveryListener(String myUsername) {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(DISCOVERY_PORT)) {
                socket.setBroadcast(true);
                byte[] buffer = new byte[1024];
                System.out.println("[FriendManager] Listening for DISCOVER messages...");

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String received = new String(packet.getData(), 0, packet.getLength());
                    if (!received.startsWith("DISCOVER|")) continue;

                    String[] parts = received.split("\\|");
                    if (parts.length != 3) continue;

                    String senderUsername = parts[1];
                    String targetUsername = parts[2];
                    String senderIp = packet.getAddress().getHostAddress();

                    if (!senderUsername.equals(myUsername) && targetUsername.equals(myUsername)) {
                        System.out.println("[FriendManager] DISCOVER received from @" + senderUsername);
                        sendResponse(senderUsername, myUsername, senderIp);
                    }
                }
            } catch (IOException e) {
                System.out.println("[FriendManager] Discovery listener error: " + e.getMessage());
            }
        }, "DiscoveryListener").start();
    }

    /**
     * Replies to a DISCOVER with a RESPONSE to the sender's IP.
     */
    private void sendResponse(String requester, String myUsername, String requesterIp) {
        String message = "RESPONSE|" + myUsername;

        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] buffer = message.getBytes();
            InetAddress address = InetAddress.getByName(requesterIp);
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, RESPONSE_PORT);
            socket.send(packet);

            System.out.println("[FriendManager] Sent RESPONSE to @" + requester + " at " + requesterIp);
        } catch (IOException e) {
            System.out.println("[FriendManager] Failed to send RESPONSE: " + e.getMessage());
        }
    }

    /**
     * Listens for incoming RESPONSE messages and updates the online user list.
     */
    public void startResponseListener() {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(RESPONSE_PORT)) {
                byte[] buffer = new byte[1024];
                System.out.println("[FriendManager] Listening for RESPONSE messages...");

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String received = new String(packet.getData(), 0, packet.getLength());
                    if (!received.startsWith("RESPONSE|")) continue;

                    String[] parts = received.split("\\|");
                    if (parts.length != 2) continue;

                    String senderUsername = parts[1];
                    String senderIp = packet.getAddress().getHostAddress();

                    onlineFriends.put(senderUsername, senderIp);
                    System.out.println("[FriendManager] Online: @" + senderUsername + " → " + senderIp);
                }
            } catch (IOException e) {
                System.out.println("[FriendManager] Response listener error: " + e.getMessage());
            }
        }, "ResponseListener").start();
    }

    /**
     * Gets all broadcast addresses for local network interfaces.
     */
    private List<InetAddress> getBroadcastAddresses() {
        List<InetAddress> broadcastList = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();

                if (!ni.isUp() || ni.isLoopback() || ni.isVirtual()) continue;

                for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
                    InetAddress broadcast = ia.getBroadcast();
                    if (broadcast != null) {
                        broadcastList.add(broadcast);
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("[FriendManager] Failed to get broadcast addresses.");
        }

        // fallback to global broadcast
        if (broadcastList.isEmpty()) {
            try {
                broadcastList.add(InetAddress.getByName("255.255.255.255"));
            } catch (UnknownHostException ignored) {}
        }

        return broadcastList;
    }
}
