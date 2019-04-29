package io.jyasonx.jsxpay.util;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.Pair;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility methods for generating key value paired string from a {@code Map} or vice versa.
 */
@Slf4j
public class StringUtils {
    public static final String UTF_8 = StandardCharsets.UTF_8.name();
    public static final String GBK = "GBK";
    public static final String COLON = ":";
    public static final String STAR = "*";
    public static final String NAMESPACE = "::";
    public static final String EQUAL_SIGN = "=";
    public static final String AMPERSAND = "&";
    public static final String AT = "@";
    public static final String FORWARD_SLASH = "/";
    public static final String DOUBLE_SLASH = "//";
    public static final String SINGLE_QUOTE = "'";
    public static final String COMMA = ",";
    public static final String EMPTY = "";
    public static final String BLANK = " ";
    public static final String DASH = "-";
    public static final String LINEFEED = "\n";
    public static final String RETURN = "\r";
    public static final String VERTICAL = "|";
    public static final String UNDERSCORE = "_";
    public static final String DOT = ".";
    public static final String QUESTION_MARK = "?";
    public static final String PLUS = "+";
    public static final String LEFT_PARENTHESE = "(";
    public static final String RIGHT_PARENTHESE = ")";
    public static final String LEFT_BRACKET = "[";
    public static final String RIGHT_BRACKET = "]";
    public static final String DOUBLE_QUOTE = "\"";
    public static final String PERCENT_SIGN = "%";
    public static final String LEFT_BRACE = "{";
    public static final String RIGHT_BRACE = "}";
    public static final int MAX_LENGTH_255 = 255;


    private StringUtils() {
        // private constructor for util class...
    }

    public static byte[] getBytesUtf8(final String string) {
        return org.apache.commons.codec.binary.StringUtils.getBytesUtf8(string);
    }

    /**
     * 适配数据库列长度,如果过长则截取.
     */
    public static String adapt(String text, int maxLength) {
        if (!Strings.isNullOrEmpty(text) && text.length() > maxLength) {
            return text.substring(0, maxLength);
        } else {
            return text;
        }
    }

    /**
     * Converts a {@code Map} to a key value paired string e.g. 'key=value&key=value...'.
     *
     * @param data the data map
     * @return the paired string
     */
    public static String pair(Map<String, String> data) {
        return data
                .entrySet()
                .stream()
                .map(e -> e.getKey() + EQUAL_SIGN + e.getValue())
                .reduce((e1, e2) -> e1 + AMPERSAND + e2)
                .orElse(AMPERSAND);
    }

    /**
     * Converts a {@code Map} to a key value paired string e.g. 'key=value&key=value...' which all
     * values are URL encoded.
     *
     * @param data     the data map
     * @param encoding the encoding
     * @return the paired string
     */
    public static String encodedPair(Map<String, String> data, String encoding) {
        return data
                .entrySet()
                .stream()
                .map(e -> e.getKey() + EQUAL_SIGN + encode(e.getValue(), encoding))
                .reduce((e1, e2) -> e1 + AMPERSAND + e2)
                .orElse(AMPERSAND);
    }

    /**
     * Reorders a key value paired string by alphabet.
     *
     * @param content the out-of-order string
     * @return the ordered & paired string
     */
    public static String sortedPair(String content) {
        return pair(splitByAlphabet(content));
    }

    /**
     * Converts a key value paired string to {@code Map}.
     *
     * @param content the key value paired string
     * @return the data map
     */
    public static Map<String, String> split(String content) {
        return Splitter
                .on(AMPERSAND)
                .trimResults()
                .omitEmptyStrings()
                .withKeyValueSeparator(EQUAL_SIGN)
                .split(content);
    }

    /**
     * Converts a key value paired string to {@code TreeMap}.
     *
     * @param content the key value paired string
     * @return the sorted data map
     */
    public static Map<String, String> splitByAlphabet(String content) {
        return new TreeMap<>(split(content));
    }

    /**
     * Extracts all values from the {@code Map}.
     *
     * @param fields the data map
     * @return the value string
     */
    public static String values(Map<String, String> fields) {
        return fields
                .entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.joining());
    }

    /**
     * Converts a key value paired string to {@code Map}.
     *
     * @param params the key value paired string
     * @return the data map
     */
    public static Map<String, String> convert2Map(String params) {
        return Splitter
                .on(AMPERSAND)
                .splitToList(params)
                .stream()
                .map(entry -> {
                    int index = entry.indexOf(EQUAL_SIGN);
                    if (index <= 0) {
                        throw new IllegalArgumentException("No valid key value pair was found");
                    }
                    return Pair.of(entry.substring(0, index), entry.substring(index + 1));
                }).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    /**
     * Translates a string as the url encoded format.
     *
     * @see URLEncoder#encode(String, String)
     */
    public static String encode(String origin) {
        try {
            return URLEncoder.encode(origin, UTF_8);
        } catch (UnsupportedEncodingException err) {
            log.warn("Failed to encode the string as URL encoded!", err);
            return origin;
        }
    }

    /**
     * Translates a string as the url encoded format.
     *
     * @see URLEncoder#encode(String, String)
     */
    public static String encode(String origin, String encoding) {
        try {
            return URLEncoder.encode(origin, encoding);
        } catch (UnsupportedEncodingException err) {
            log.warn("Failed to encode the string as URL encoded!", err);
            return origin;
        }
    }

    /**
     * Translates an encoded string from the url encoded format.
     *
     * @see URLEncoder#encode(String, String)
     */
    public static String decode(String encoded) {
        try {
            return URLDecoder.decode(encoded, UTF_8);
        } catch (UnsupportedEncodingException err) {
            log.warn("Failed to decode the string from URL encoded!", err);
            return encoded;
        }
    }

    /**
     * Concatenate string of objects in array. Every object string is contained within single quotes.
     * The result is like:'xxx','xxx','xxx'.
     */
    public static String join(final Object[] objects) {
        return Arrays
                .stream(objects)
                .map(t -> SINGLE_QUOTE + t.toString() + SINGLE_QUOTE)
                .reduce((result, element) -> result + COMMA + element)
                .orElse(EMPTY);
    }

    /**
     * Join every element in collection by comma.
     */
    public static String joinWithComma(Collection<String> set) {
        return Joiner.on(COMMA).skipNulls().join(set);
    }

    /**
     * Split the content with comma.
     */
    public static List<String> splitWithComma(String content) {
        return Splitter
                .on(COMMA)
                .omitEmptyStrings()
                .trimResults()
                .splitToList(Strings.nullToEmpty(content));
    }

    /**
     * Encode bytes to a base64 encoded string.
     */
    public static String encodeBase64(byte[] content) {
        return Base64.encodeBase64String(content);
    }

    /**
     * Encode content to a base64 encoded string.
     */
    public static String encodeBase64(String content) {
        return encodeBase64(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decode content from base64 encoded bytes.
     */
    public static String decodeBase64(byte[] bytes) {
        return new String(Base64.decodeBase64(bytes), StandardCharsets.UTF_8);
    }

    /**
     * Decode content from a base64 encoded string.
     */
    public static String decodeBase64(String content) {
        return decodeBase64(content.getBytes(StandardCharsets.UTF_8));
    }
}
