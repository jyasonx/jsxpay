package io.jyasonx.jsxpay.channel.bean;

import io.jyasonx.jsxpay.channel.Response;
import io.jyasonx.jsxpay.channel.Transaction;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class TransactionResponse extends Response {

    private String orderNo;
    private Transaction transaction;
}
