package main.java.com.p2pchatapplication;

import java.net.*;
import java.util.Enumeration;

public class NetworkUtils {

    // ✅ Method to check LAN connectivity
    public static boolean isConnectedToLAN() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface netIf = interfaces.nextElement();
                if (netIf.isLoopback() || !netIf.isUp()) continue; // Skip loopback & inactive interfaces
                
                Enumeration<InetAddress> addresses = netIf.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) { // ✅ Found an IPv4 LAN address
                        return true;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return false; // ❌ No LAN connection detected
    }
    public static void main(String[] args) {
        System.out.println(isConnectedToLAN());
    }
}
   