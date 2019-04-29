package io.jyasonx.jsxpay.channel;

import io.jyasonx.jsxpay.common.Operation;
import io.jyasonx.jsxpay.common.TransactionType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public abstract class Request {
    private final RequestType type;

    private final LocalDateTime createdTime = LocalDateTime.now();
    private TransactionType transactionType;
    private Operation operation = Operation.execute;
    private String content;

    private Config config;

    public Request(RequestType type) {
        this.type = type;
    }
}
