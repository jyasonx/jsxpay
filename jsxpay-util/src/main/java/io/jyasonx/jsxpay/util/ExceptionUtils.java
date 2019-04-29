package io.jyasonx.jsxpay.util;

import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
public class ExceptionUtils {

    public static String toString(Throwable throwable) {
        try (StringWriter stringWriter = new StringWriter();
             PrintWriter writer = new PrintWriter(stringWriter)){
            throwable.printStackTrace(writer);
            return stringWriter.toString();

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            // ignore
        }

        return null;
    }
}
