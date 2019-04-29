package io.jyasonx.jsxpay.util;

import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Objects;

/**
 * Utility methods for IP related operations.
 */
@Slf4j
public class IpUtils {
    // The IP address start with 127 is the loopback address which is used for internal testing only.
    public static final String LOOPBACK_ADDRESS = "127.0.0.1";
    public static final String DEFAULT_REGEX = "bond0|eth0|en0";

    private IpUtils() {
    }

    /**
     * Gets the IPv4 address of running host.
     */
    public static String ipv4Address() {
        return ipv4Address(null);
    }

    public static String ipv4AddressLocal() {
        return ipv4Address(DEFAULT_REGEX);
    }

    public static String ipv4Address(String regex) {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (Objects.nonNull(regex) && !networkInterface.getDisplayName().matches(regex)) {
                    continue;
                }
                Enumeration<InetAddress> ipAddresses = networkInterface.getInetAddresses();
                while (ipAddresses.hasMoreElements()) {
                    InetAddress ipAddress = ipAddresses.nextElement();
                    String hostAddressName = ipAddress.getHostAddress();
                    if (ipAddress instanceof Inet4Address) {
                        return hostAddressName;
                    }
                }
            }
            return LOOPBACK_ADDRESS;
        } catch (Exception err) {
            log.warn("Failed to load IP address with error {}.", err);
            return LOOPBACK_ADDRESS;
        }
    }


}
