package io.jyasonx.jsxpay.channel.wechat;

import freemarker.template.Configuration;
import freemarker.template.Template;
import io.jyasonx.jsxpay.channel.AbstractConverter;
import io.jyasonx.jsxpay.channel.Request;
import io.jyasonx.jsxpay.channel.ThirdpartyException;
import io.jyasonx.jsxpay.channel.bean.TransactionQueryRequest;
import io.jyasonx.jsxpay.channel.bean.TransactionQueryResponse;
import io.jyasonx.jsxpay.channel.bean.TransactionRequest;
import io.jyasonx.jsxpay.channel.bean.TransactionResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class WechatConverter extends AbstractConverter {

    private Template transactionTemplate;

    public WechatConverter() {
        Configuration configuration = FREEMARKER_HELPER.getConfiguration();
        try {
            transactionTemplate = configuration.getTemplate("WECHAT_Transaction.ftl");
        } catch (IOException ex) {
            throw new ThirdpartyException("Failed to initialize templates.", ex);
        }
    }

    @Override
    protected String from(TransactionRequest request) {
        return super.from(request);
    }

    @Override
    protected TransactionResponse toTransactionResponse(String content, Request request) {
        return super.toTransactionResponse(content, request);
    }

    @Override
    protected String from(TransactionQueryRequest request) {
        return super.from(request);
    }

    @Override
    protected TransactionQueryResponse toTransactionQueryResponse(String content, Request request) {
        return super.toTransactionQueryResponse(content, request);
    }
}
