package io.jyasonx.jsxpay.channel.bean;

import io.jyasonx.jsxpay.channel.Request;
import io.jyasonx.jsxpay.channel.RequestType;
import io.jyasonx.jsxpay.channel.Transaction;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class TransactionQueryRequest extends Request {

    private String orderNo;
    private String queryOrderNo;
    private boolean batch;

    private List<Transaction> transactions = new ArrayList<>();

    public TransactionQueryRequest() {
        super(RequestType.TRANSACTION_QUERY);
    }
}
