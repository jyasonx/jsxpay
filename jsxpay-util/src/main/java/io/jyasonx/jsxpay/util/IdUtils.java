package io.jyasonx.jsxpay.util;

import com.google.common.base.Preconditions;

import java.util.UUID;

/**
 * Utility methods for ID related operations.
 */
public class IdUtils {
    private static final String STR_ZERO = "0000000000000000000000000000000000000000";

    private IdUtils() {
        // private constructor for util class...
    }

    /**
     * Creates an uuid from the nano time of system.
     *
     * @return the uuid string
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * Creates an uuid from the nano time of system without dash.
     *
     * @return the uuid string
     */
    public static String uuidWithoutDash() {
        return uuid().replaceAll(StringUtils.DASH, StringUtils.EMPTY);
    }

    /**
     * Format Id xxx -> 0000xxx.
     */
    public static String formatId(String originId, int len) {
        Preconditions.checkArgument(len <= STR_ZERO.length(), "Len must <=" + STR_ZERO.length());
        if (originId.length() >= len) {
            return originId;
        }
        return STR_ZERO.substring(originId.length(), len) + originId;
    }
}
