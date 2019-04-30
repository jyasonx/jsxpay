package io.jyasonx.jsxpay.channel.wechat;

import io.jyasonx.jsxpay.channel.AbstractHttpProcessor;
import io.jyasonx.jsxpay.channel.Request;
import io.jyasonx.jsxpay.channel.Response;
import io.jyasonx.jsxpay.channel.ThirdpartyException;
import io.jyasonx.jsxpay.common.ChannelType;
import io.jyasonx.jsxpay.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.security.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.jyasonx.jsxpay.util.StringUtils.DASH;

@Slf4j
public class WechatProcessor extends AbstractHttpProcessor {
    public static final String BEAN_NAME = ChannelType.WECHAT_NAME + NAME_SUFFIX;
    private static final String URL_UNIFIED_ORDER = "/unifiedorder";
    private static final String URL_ORDER_QUERY = "/orderquery";

    private static final Header HEADER_XML
            = new BasicHeader("Content-Type", "text/xml; charset=utf8");

    private static final Map<String, HttpClient> httpClients = new ConcurrentHashMap<>();

    private WechatConverter converter;
    private WechatCryptor cryptor;

    public WechatProcessor(HttpClient httpClient, WechatConverter converter, WechatCryptor cryptor) {
        // This httpClient will not be used.
        super(httpClient);
        this.converter = converter;
        this.cryptor = cryptor;
    }

    @Override
    public <T extends Response> T execute(Request request) {
        try {
            String requestString = converter.writeTo(request);
            String signedRequestString = cryptor.sign(requestString, request);
            log.info("signed string: {}", signedRequestString);

            request.setContent(signedRequestString);

            HttpRequestBase requestBase = compose(request, signedRequestString);

            HttpEntity responseEntity = doExecute(requestBase);
            String responseString = EntityUtils.toString(responseEntity, request.getConfig().getEncoding());
            log.info("response string: {}", responseString);

            cryptor.verify(responseString, request);

            Response response = converter.readFrom(responseString, request);
            response.setContent(responseString);

            //noinspection unchecked
            return (T) response;
        } catch (IOException ex) {
            log.error("Failed to read the HTTP entity from response!", ex);
            throw new ThirdpartyException("Error reading the HTTP entity from response", ex);
        }

    }

    private HttpRequestBase compose(Request request, String content) {
        String contextUrl;
        switch (request.getType()) {
            case TRANSACTION:
                contextUrl = URL_UNIFIED_ORDER;
                break;
            case TRANSACTION_QUERY:
                contextUrl = URL_ORDER_QUERY;
                break;
            default:
                throw new IllegalArgumentException("Request type not supported by WeChat Pay");
        }
        HttpPost post = new HttpPost(request.getConfig().getBaseUrl() + contextUrl);
        post.setHeader(HEADER_XML);
        post.setEntity(new StringEntity(content, request.getConfig().getEncoding()));
        return post;
    }

    /**
     * used when http client need certificate
     */
    private HttpClient getHttpClient(Request request) {

        String keyName = request.getConfig().getChannelType().name() + DASH + request.getConfig().getChannelNo();
        if (httpClients.containsKey(keyName)) {
            return httpClients.get(keyName);
        }

        try {
            KeyStore keyStore = SecurityUtils.getKeyStore(request.getConfig().getPrivateKeyType(),
                    request.getConfig().getPrivateKey(), request.getConfig().getPrivateKeyPassword(),
                    SecurityUtils.DEFAULT_PROVIDER);

            HttpClient httpClient = HttpClients
                    .custom()
                    .setDefaultRequestConfig(RequestConfig
                            .custom()
                            .setConnectTimeout(30000)
                            .setConnectionRequestTimeout(30000)
                            .setSocketTimeout(30000)
                            .build())
                    .setSSLContext(SSLContextBuilder
                            .create()
                            .setProtocol("TLSv1")
                            .loadKeyMaterial(keyStore, request.getConfig().getPrivateKeyPassword().toCharArray())
                            .build())
                    .setSSLHostnameVerifier(SSLConnectionSocketFactory.getDefaultHostnameVerifier())
                    .build();
            httpClients.put(keyName, httpClient);
            return httpClient;
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException | UnrecoverableKeyException ex) {
            log.error("Wechat httpClient building failed.", ex);
            throw new ThirdpartyException(ex.getMessage(), ex);
        }
    }

}
