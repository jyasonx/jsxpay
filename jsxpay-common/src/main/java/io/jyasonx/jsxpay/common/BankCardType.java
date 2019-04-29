package io.jyasonx.jsxpay.common;

import lombok.Getter;

@Getter
public enum BankCardType {
    UNKNOWN("未知类型"),
    DEBIT_CARD("借记卡"),
    CREDIT_CARD("贷记卡"),
    QUASI_CREDIT_CARD("准贷记卡"),
    PREPAID_CARD("预付卡"),
    ;

    private String name;

    BankCardType(String name) {
        this.name = name;
    }
}
