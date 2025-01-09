package com.JIRSendMod.network;

import java.net.*;
import java.util.Enumeration;

/**
 * Network utility class for local address resolution
 */
public class NetworkUtils {

    /**
     * Identify first non-virtual interface
     * @return corresponding IP address
     * @throws SocketException
     */

    public static InetAddress getFirstPublicIPAddress() throws SocketException {
        System.out.println("Calling getFirstPublicIPAddress");

        Enumeration<NetworkInterface> networkInterfaces =
            NetworkInterface.getNetworkInterfaces();

        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();

            // Ignore interfaces that are not up or are virtual (e.g., Docker interfaces)
            if (!networkInterface.isUp() || networkInterface.isVirtual()) {
                continue;
            }

            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();

                // Ignore loopback and link-local addresses
                if (
                    !inetAddress.isLoopbackAddress() &&
                    inetAddress instanceof Inet4Address
                ) {
                    return inetAddress;
                }
            }
        }
        return null; // No public address found
    }
}
