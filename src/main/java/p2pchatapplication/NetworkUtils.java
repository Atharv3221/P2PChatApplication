package p2pchatapplication;

import java.net.*;
import java.util.*;

public class NetworkUtils {
    public static String getMyIPAddress() {
        try {
            for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();) {
                NetworkInterface ni = e.nextElement();
                for (Enumeration<InetAddress> ee = ni.getInetAddresses(); ee.hasMoreElements();) {
                    InetAddress ia = ee.nextElement();
                    if (!ia.isLoopbackAddress() && ia instanceof Inet4Address) {
                        return ia.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            System.out.println("❌ IP Error: " + e.getMessage());
        }
        return "127.0.0.1";
    }

    public static List<String> getLocalSubnetIPs() {
        List<String> ips = new ArrayList<>();
        String myIP = getMyIPAddress();
        String subnet = myIP.substring(0, myIP.lastIndexOf("."));

        for (int i = 1; i < 255; i++) {
            ips.add(subnet + "." + i);
        }
        return ips;
    }
}

