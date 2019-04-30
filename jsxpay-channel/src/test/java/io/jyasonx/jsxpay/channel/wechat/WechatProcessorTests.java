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
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static io.jyasonx.jsxpay.channel.wechat.WechatConverter.CODE_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class WechatProcessorTests extends AbstractBaseTests {

    private WechatProcessor processor;
    private Config config;

    @Before
    public void setUp() throws Exception {
        config = getConfig(ChannelType.WECHAT, DEFAULT_CHANNEL_NO);
        processor = new WechatProcessor(httpClient(), new WechatConverter(), new WechatCryptor());
    }

    @Test
    public void testTransaction() {
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
        TransactionResponse response = processor.execute(request);

        log.info("response: {}", response);

        assertThat(response).isNotNull();
        assertThat(response.getTransaction().getCode()).isEqualTo(CODE_SUCCESS);
        assertThat(response.getTransaction().getStatus()).isEqualTo(TransactionStatus.PROCESSING);
    }

}
