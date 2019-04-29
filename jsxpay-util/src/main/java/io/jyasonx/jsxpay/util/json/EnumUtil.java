package io.jyasonx.jsxpay.util.json;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnumUtil {
    /**
     * 类似于Enum.valueOf(enumType, name)，只是在这个name不存在的时候返回null，而不是抛Exception
     */
    public static <T extends Enum<T>> T safeValueOf(Class<T> enumType, String name) {
        return safeValueOf(enumType, name, null);
    }

    /**
     * 类似于Enum.valueOf(enumType, name)，只是在这个name不存在的时候返回defaultValue，而不是抛Exception
     */
    public static <T extends Enum<T>> T safeValueOf(Class<T> enumType, String name, T defaultValue) {
        try {
            return Enum.valueOf(enumType, name);
        } catch (Exception err) {
            log.error("Error in getting the enum constant of enum type with the specified name", err);
            return defaultValue;
        }
    }
}
