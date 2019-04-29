package io.jyasonx.jsxpay.common;

import lombok.Getter;

@Getter
public enum PaymentType {
    GATEWAY("网关", "用户在浏览器跳转到支付平台"),
    DIRECT("直连", "用户请求后台转发到支付平台api"),
    PAYMENT_CODE("付款码", "用户打开付款码，商户扫码后提交完成支付"),
    @SuppressWarnings("SpellCheckingInspection")
    JSAPI("JSAPI", "用户在APP内的商户H5页面唤起JSSDK完成支付"),
    NATIVE("扫码", "用户扫描商户生成的二维码后完成支付"),
    APP("APP", "商户APP集成SDK，用户点击跳转到APP内完成支付"),
    H5("H5", "用户在APP以外的手机浏览器请求支付的场景唤起SDK"),
    MINI_PROGRAM("小程序", "用户在小程序中使用APP支付的场景"),
    FACE("刷脸", "刷脸完成支付");

    private String name;
    private String desc;

    PaymentType(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
}
