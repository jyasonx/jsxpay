package io.jyasonx.jsxpay.common;

import lombok.Getter;

@SuppressWarnings("SpellCheckingInspection")
@Getter
public enum ChannelType {

    WECHAT("微信"),
    ALIPAY("支付宝"),
    JDFINANCE("京东白条"),
    ;

    private String name;

    ChannelType(String name) {
        this.name = name;
    }

    public static final String WECHAT_NAME = "WECHAT";
    public static final String ALIPAY_NAME = "ALIPAY";
    public static final String JDFINANCE_NAME = "JDFINANCE";
}
