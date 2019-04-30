package io.jyasonx.jsxpay.channel.wechat.mapper;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XStreamAlias("xml")
public class ResponseMapper {

    // always returned
    @XStreamAlias("return_code")
    private String code;

    @XStreamAlias("return_msg")
    private String message;

    // only if the code is SUCCESS
    @SuppressWarnings("SpellCheckingInspection")
    @XStreamAlias("appid")
    private String appId;

    @XStreamAlias("mch_id")
    private String merchantNo;

    @XStreamAlias("device_info")
    private String deviceInfo;

    @XStreamAlias("nonce_str")
    private String nonceString;

    @XStreamAlias("sign")
    private String signature;

    @XStreamAlias("out_trade_no")
    private String outTradeNo;

    @XStreamAlias("attach")
    private String attach;

    @XStreamAlias("result_code")
    private String resultCode;

    @XStreamAlias("trade_state")
    private String tradeState;

    @XStreamAlias("err_code")
    private String errorCode;

    @XStreamAlias("err_code_des")
    private String errorMessage;

    // only if the return code & result code are SUCCESS
    @XStreamAlias("trade_type")
    private String tradeType;

    @XStreamAlias("prepay_id")
    private String prepayId;

    @XStreamAlias("code_url")
    private String codeUrl;

    @XStreamAlias("time_end")
    private String finishedTime;

    @XStreamAlias("trade_state_desc")
    private String tradeStatusDescription;
}
