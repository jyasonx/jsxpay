package io.jyasonx.jsxpay.channel;

import io.jyasonx.jsxpay.common.ChannelType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class Config {

    private String channelNo;
    private ChannelType channelType;

    private String baseUrl;
    private String callbackUrl;
    private String queryUrl;
    private String returnUrl;

    private String encoding;

    // secret
    private String merchantNo;
    private String username;
    private String password;
    private String privateKeyType;
    private String privateKey;
    private String privateKeyPassword;
    private String publicKeyType;
    private String publicKey;
    private String signatureAlgorithm;

    private String appId;
    private String secretKey;

    private Map<String, BankMapping> bankMappings = new HashMap<>();

}
