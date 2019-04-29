package io.jyasonx.jsxpay.channel.bean;

import io.jyasonx.jsxpay.channel.Response;
import io.jyasonx.jsxpay.channel.Transaction;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class TransactionQueryResponse extends Response {
    private String orderNo;
    private String queryOrderNo;

    private List<Transaction> transactions = new ArrayList<>();
}
