package io.jyasonx.jsxpay.common;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private String code;
    private String msg;

    public ApiException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public ApiException(String code, String msg, Throwable throwable) {
        super(msg, throwable);
        this.code = code;
        this.msg = msg;
    }


}
