package p2pchatapplication;

import java.io.IOException;

/**
 * Tests if the device is truly connected to LAN by pinging the gateway.
 */
public class NetworkTest {

    public static void main(String[] args) {
        // You can adjust this to match your router's IP if different
        String gatewayIP = "192.168.1.1";

        boolean isConnected = isReachable(gatewayIP, 1000); // 1-second timeout
        System.out.println(isConnected ? "true --> connected to LAN" : "false --> not connected to LAN");
    }

    /**
     * Pings a specific IP address to test connectivity.
     * @param ip the IP address to ping
     * @param timeoutMillis timeout in milliseconds
     * @return true if reachable, false otherwise
     */
    public static boolean isReachable(String ip, int timeoutMillis) {
        try {
            return java.net.InetAddress.getByName(ip).isReachable(timeoutMillis);
        } catch (IOException e) {
            return false;
        }
    }
}
