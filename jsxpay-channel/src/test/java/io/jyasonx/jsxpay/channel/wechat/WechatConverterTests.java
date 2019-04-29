package io.jyasonx.jsxpay.channel.wechat;

import io.jyasonx.jsxpay.channel.AbstractBaseTests;
import io.jyasonx.jsxpay.channel.Config;
import io.jyasonx.jsxpay.channel.Transaction;
import io.jyasonx.jsxpay.channel.bean.TransactionRequest;
import io.jyasonx.jsxpay.common.ChannelType;
import io.jyasonx.jsxpay.common.TransactionType;
import io.jyasonx.jsxpay.util.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        String requestContent = converter.from(request);

        log.info("from transaction content: {}", requestContent);

    }
}
