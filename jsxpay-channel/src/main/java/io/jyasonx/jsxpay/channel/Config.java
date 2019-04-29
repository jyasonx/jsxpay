package io.jyasonx.jsxpay.channel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class Config {

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

    private Map<String, BankMapping> bankMappings = new HashMap<>();

}
