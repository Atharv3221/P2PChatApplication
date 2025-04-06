package p2pchatapplication;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class FriendManager {
    private static final int CHAT_PORT = 5000;
    private static final int DISCOVERY_TIMEOUT = 500;
    private static final Map<String, String> activeFriends = new ConcurrentHashMap<>();

    public static void addActiveFriend(String username, String ip) {
        activeFriends.put(username, ip);
        System.out.println("🔗 Friend online: @" + username);
    }

    public static String getFriendIP(String username) {
        return activeFriends.get(username);
    }

    public static void printActiveFriends() {
        if (activeFriends.isEmpty()) {
            System.out.println("No friends online.");
        } else {
            activeFriends.forEach((u, ip) -> System.out.println("@" + u + " -> " + ip));
        }
    }

    public static void discoverAllFriends() {
        for (String friend : UserManager.getAllFriends()) {
            discoverUser(friend);
        }
    }

    public static void discoverUser(String targetUsername) {
        List<String> ips = NetworkUtils.getLocalSubnetIPs();
        String myUsername = Main.getMyUsername();

        for (String ip : ips) {
            if (ip.equals(NetworkUtils.getMyIPAddress())) continue;

            new Thread(() -> {
                try (Socket socket = new Socket()) {
                    socket.connect(new InetSocketAddress(ip, CHAT_PORT), DISCOVERY_TIMEOUT);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println("{\"type\":\"DISCOVER\",\"username\":\"" + myUsername + "\"}");
                } catch (Exception ignored) {}
            }).start();
        }
    }
}
