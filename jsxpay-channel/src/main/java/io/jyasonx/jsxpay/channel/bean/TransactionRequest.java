package io.jyasonx.jsxpay.channel.bean;

import io.jyasonx.jsxpay.channel.Request;
import io.jyasonx.jsxpay.channel.RequestType;
import io.jyasonx.jsxpay.channel.Transaction;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class TransactionRequest extends Request {

    private String orderNo;
    private Transaction transaction;

    public TransactionRequest() {
        super(RequestType.TRANSACTION);
    }
}
