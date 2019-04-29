package io.jyasonx.jsxpay.common;

import lombok.Getter;

@Getter
public enum TransactionType {
    UNKNOWN("未知"),
    INSTALLMENT("分期"),
    WITHHOLD("代扣"),
    PAY("代付"),
    RECHARGE("充值"),
    WITHDRAW("提现"),
    TRANSFER("转账"),
    REFUND("退款");

    private String name;

    TransactionType(String name) {
        this.name = name;
    }
}
