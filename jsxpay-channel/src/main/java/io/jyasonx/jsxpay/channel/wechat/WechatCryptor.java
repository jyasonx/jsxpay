package io.jyasonx.jsxpay.channel.wechat;

import com.thoughtworks.xstream.XStream;
import io.jyasonx.jsxpay.channel.Request;
import io.jyasonx.jsxpay.channel.ThirdpartyException;
import io.jyasonx.jsxpay.util.SecurityUtils;
import io.jyasonx.jsxpay.util.StringUtils;
import io.jyasonx.jsxpay.util.xstream.NestedMapConverter;
import io.jyasonx.jsxpay.util.xstream.OrdinalType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import static io.jyasonx.jsxpay.channel.wechat.WechatConverter.CODE_SUCCESS;
import static io.jyasonx.jsxpay.util.StringUtils.AMPERSAND;
import static io.jyasonx.jsxpay.util.StringUtils.EQUAL_SIGN;

@Slf4j
public class WechatCryptor {

    private static final String XML_NODE_ROOT = "xml";
    private static final String XML_NODE_SIGN = "sign";
    private static final String XML_NAME_KEY = "key";

    private static final String XML_NODE_CODE = "result_code";

    private static final Pattern SIGNATURE_REPLACEMENT = Pattern.compile("(?<=<sign>)(.*?)(?=</sign>)");

    private final XStream stream;

    public WechatCryptor() {
        stream = new XStream();
        XStream.setupDefaultSecurity(stream);
        stream.allowTypes(new Class[]{TreeMap.class});
        stream.alias(XML_NODE_ROOT, TreeMap.class);
        stream.registerConverter(new NestedMapConverter(OrdinalType.ASCII));
    }

    public String sign(String content, Request request) {

        try {
            @SuppressWarnings("unchecked")
            Map<String, String> elements = (Map) stream.fromXML(content);
            elements.remove(XML_NODE_SIGN);
            String pairs = StringUtils.pair(elements);
            String data = pairs + AMPERSAND + XML_NAME_KEY + EQUAL_SIGN + request.getConfig().getSecretKey();
            String sign = Hex.encodeHexString(SecurityUtils.digest(request.getConfig().getSignatureAlgorithm(),
                    data.getBytes(request.getConfig().getEncoding()))).toUpperCase();
            return SIGNATURE_REPLACEMENT.matcher(content).replaceFirst(sign);
        } catch (UnsupportedEncodingException ex) {
            log.error("Failed to sign the request due to an exception!", ex);
            throw new ThirdpartyException("Error signing the request", ex);
        }
    }

    public void verify(String content, Request request) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, String> elements = (Map) stream.fromXML(content);

            if (!CODE_SUCCESS.equals(elements.get(XML_NODE_CODE))) {
                return;
            }
            String originalSign = elements.remove(XML_NODE_SIGN);
            String pairs = StringUtils.pair(elements);
            String data = pairs + AMPERSAND + XML_NAME_KEY + EQUAL_SIGN + request.getConfig().getSecretKey();
            String sign = Hex.encodeHexString(SecurityUtils.digest(request.getConfig().getSignatureAlgorithm(),
                    data.getBytes(request.getConfig().getEncoding()))).toUpperCase();

            if (!sign.equalsIgnoreCase(originalSign)) {
                log.error("The invalid signature '{}' was found in response '{}'!", originalSign, data);
                throw new ThirdpartyException("Invalid signature found in response");
            }
        } catch (UnsupportedEncodingException ex) {
            log.error("Failed to sign the request due to an exception!", ex);
            throw new ThirdpartyException("Error signing the request", ex);
        }
    }

}
