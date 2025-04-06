package p2pchatapplication;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetworkUtils {

    /**
     * Returns the machine's local IP address (on LAN).
     *
     * @return the LAN IP address as a String
     */
    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();

                if (iface.isLoopback() || !iface.isUp() || iface.isVirtual()) continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    if (!addr.isLoopbackAddress() && addr.getHostAddress().indexOf(":") == -1) {
                        return addr.getHostAddress();  // IPv4 only
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[NetworkUtils] Error getting IP: " + e.getMessage());
        }
        return "127.0.0.1";
    }
}
