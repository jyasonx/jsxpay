package io.jyasonx.jsxpay.channel.wechat;

import io.jyasonx.jsxpay.channel.AbstractBaseTests;
import io.jyasonx.jsxpay.channel.Config;
import io.jyasonx.jsxpay.channel.bean.TransactionRequest;
import io.jyasonx.jsxpay.common.ChannelType;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


@Slf4j
public class WechatCryptorTests extends AbstractBaseTests {

    private Config config;
    private WechatCryptor cryptor;

    @Before
    public void setUp() throws Exception {
        config = getConfig(ChannelType.WECHAT, DEFAULT_CHANNEL_NO);
        cryptor = new WechatCryptor();
    }

    @Test
    public void testSign() {
        String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml><appid>wx99bcf174724d0ae0</appid><mch_id>1251462001</mch_id><nonce_str>52dfdd18186a42cabb96f98882c4e69d</nonce_str><out_trade_no>c91592e61b2f4fe98bb9af530b2831e4</out_trade_no><product_id>c91592e61b2f4fe98bb9af530b2831e4</product_id><sign_type>MD5</sign_type><body>test transaction</body><detail>test transaction</detail><notify_url>http://127.0.0.1</notify_url><fee_type>CNY</fee_type><total_fee>100</total_fee><time_start>20190430153204</time_start><time_expire>20190430163204</time_expire><trade_type>NATIVE</trade_type><sign></sign></xml>";
        TransactionRequest request = new TransactionRequest();
        request.setConfig(config);
        String sign = cryptor.sign(content, request);
        assertThat(sign).isNotEmpty().contains("B42C02F1B1B6ADF60F4392569CDE446E");
    }

    @Test
    public void testVerify() {
        String content = "<xml><return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "<return_msg><![CDATA[OK]]></return_msg>\n" +
                "<appid><![CDATA[wx99bcf174724d0ae0]]></appid>\n" +
                "<mch_id><![CDATA[1251462001]]></mch_id>\n" +
                "<nonce_str><![CDATA[lbMeN1BDlecy00Rh]]></nonce_str>\n" +
                "<sign><![CDATA[0774DB3F7319034CE1B78F966820DEE5]]></sign>\n" +
                "<result_code><![CDATA[SUCCESS]]></result_code>\n" +
                "<prepay_id><![CDATA[wx30165715577881d4d4de0c5d2282184633]]></prepay_id>\n" +
                "<trade_type><![CDATA[NATIVE]]></trade_type>\n" +
                "<code_url><![CDATA[weixin://wxpay/bizpayurl?pr=dSLgwqI]]></code_url>\n" +
                "</xml>";

        TransactionRequest request = new TransactionRequest();
        request.setConfig(config);
        cryptor.verify(content, request);
    }
}
