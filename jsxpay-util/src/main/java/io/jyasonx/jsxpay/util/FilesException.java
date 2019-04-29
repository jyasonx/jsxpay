package io.jyasonx.jsxpay.util;

public class FilesException extends RuntimeException {

    public FilesException(String message) {
        super(message);
    }

    public FilesException(Throwable cause) {
        super(cause);
    }

    public FilesException(String message, Throwable cause) {
        super(message, cause);
    }
}