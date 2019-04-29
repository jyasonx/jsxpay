package io.jyasonx.jsxpay.common;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CREATED("已创建"),
    PREPARED("已下单"),
    SUCCEED("成功"),
    FAILED("失败"),
    PROCESSING("处理中"),
    CLOSED("已关闭"),
    ;

    private String name;

    OrderStatus(String name) {
        this.name = name;
    }
}
