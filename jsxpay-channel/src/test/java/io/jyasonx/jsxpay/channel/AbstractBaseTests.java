package io.jyasonx.jsxpay.channel;

import io.jyasonx.jsxpay.common.ChannelType;
import io.jyasonx.jsxpay.util.StringUtils;
import io.jyasonx.jsxpay.util.json.JsonProviderHolder;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.SecureRandom;

import static io.jyasonx.jsxpay.util.StringUtils.DASH;

public abstract class AbstractBaseTests {
    protected static final String DEFAULT_CHANNEL_NO = "DEFAULT";
    protected static final String DEFAULT_ENV = "dev";
    protected static final String ENV = System.getProperty("env", DEFAULT_ENV);

    private static final String BASE_DIRECTION = "thirdparty-config";
    private static final String SEPARATOR = System.getProperty("file.separator");
    private static final String SUFFIX = ".json";

    protected Config getConfig(ChannelType type, String channelNo) throws IOException {
        String filename = BASE_DIRECTION + SEPARATOR + ENV + SEPARATOR + type.name() + DASH + channelNo + SUFFIX;
        String content = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream(filename),
                Charset.forName(StringUtils.UTF_8));
        return JsonProviderHolder.JACKSON.parse(content, Config.class);
    }

    protected HttpClient httpClient() throws Exception {
        return HttpClients
                .custom()
                .setDefaultRequestConfig(RequestConfig
                        .custom()
                        .setConnectTimeout(30000)
                        .setConnectionRequestTimeout(30000)
                        .setSocketTimeout(30000)
                        .build())
                .setSSLContext(SSLContextBuilder
                        .create()
                        .setProtocol("TLS")
                        .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                        .setSecureRandom(new SecureRandom())
                        .build())
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
    }

    protected static String getIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

}
