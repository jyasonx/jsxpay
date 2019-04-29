package io.jyasonx.jsxpay.channel;

@SuppressWarnings("SpellCheckingInspection")
public class ThirdpartyException extends RuntimeException {

    public ThirdpartyException(String message) {
        super(message);
    }

    public ThirdpartyException(String message, Throwable cause) {
        super(message, cause);
    }
}
