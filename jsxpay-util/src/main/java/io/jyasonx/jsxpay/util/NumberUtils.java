package io.jyasonx.jsxpay.util;

/**
 * Utility methods for formatting & parsing number values.
 */
public class NumberUtils {

    /**
     * Convert int to byte[].
     */
    public static byte[] toBytes(int num) {
        return new byte[]{
                (byte) ((num >> 24) & 0xFF),
                (byte) ((num >> 16) & 0xFF),
                (byte) ((num >> 8) & 0xFF),
                (byte) (num & 0xFF)
        };
    }

    /**
     * Convert byte[] to int.
     */
    public static int toInt(byte[] bytes) {
        return bytes[3] & 0xFF
                | (bytes[2] & 0xFF) << 8
                | (bytes[1] & 0xFF) << 16
                | (bytes[0] & 0xFF) << 24;
    }

}
