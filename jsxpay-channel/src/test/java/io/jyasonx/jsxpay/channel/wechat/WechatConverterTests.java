package io.jyasonx.jsxpay.channel.wechat;

import io.jyasonx.jsxpay.channel.AbstractBaseTests;
import io.jyasonx.jsxpay.channel.Config;
import io.jyasonx.jsxpay.channel.Transaction;
import io.jyasonx.jsxpay.channel.bean.TransactionRequest;
import io.jyasonx.jsxpay.channel.bean.TransactionResponse;
import io.jyasonx.jsxpay.common.ChannelType;
import io.jyasonx.jsxpay.common.TransactionStatus;
import io.jyasonx.jsxpay.common.TransactionType;
import io.jyasonx.jsxpay.util.IdUtils;
import io.jyasonx.jsxpay.util.json.JsonProviderHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import javax.transaction.TransactionRequiredException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("SpellCheckingInspection")
@Slf4j
public class WechatConverterTests extends AbstractBaseTests {

    private WechatConverter converter;
    private Config config;

    @Before
    public void setUp() throws Exception {
        config = getConfig(ChannelType.WECHAT, DEFAULT_CHANNEL_NO);
        converter = new WechatConverter();
    }

    @Test
    public void testFromTransaction() {
        TransactionRequest request = new TransactionRequest();
        request.setConfig(config);
        request.setOrderNo(IdUtils.uuid());
        request.setTransactionType(TransactionType.WITHHOLD);
        request.setTransaction(Transaction.builder()
                .channelSerialNo(IdUtils.uuidWithoutDash())
                .description("test transaction")
                .amount(BigDecimal.valueOf(1))
                .expireTime(LocalDateTime.now().plusHours(1L))
                .build());
        log.info("json: {}", JsonProviderHolder.JACKSON.convertObj(request.getTransaction()));
        String requestContent = converter.from(request);

        log.info("from transaction content: {}", requestContent);
    }

    @Test
    public void testToTransaction() {
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
        TransactionResponse response = converter.toTransactionResponse(content, request);
        assertThat(response).isNotNull();
        assertThat(response.getTransaction().getStatus()).isEqualTo(TransactionStatus.PROCESSING);
        assertThat(response.getTransaction().getThirdpartyPrepayNo()).isEqualTo("wx30165715577881d4d4de0c5d2282184633");
    }
}
