package io.jyasonx.jsxpay.channel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public abstract class Response {

    private String code;
    private String message;
    private String content;
    private final LocalDateTime createdTime = LocalDateTime.now();
}
