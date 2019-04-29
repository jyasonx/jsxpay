package io.jyasonx.jsxpay.channel;

import freemarker.template.Template;
import io.jyasonx.jsxpay.channel.bean.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public abstract class AbstractConverter implements Converter {

    @SuppressWarnings("SpellCheckingInspection")
    protected static final FreeMarkerHelper FREEMARKER_HELPER = new FreeMarkerHelper(
            "/templates/wechat",
            "/templates/alipay",
            "/templates/jdfinance");

    protected String render(Template template, Map<String, Object> data) {
        return FREEMARKER_HELPER.render(template, data);
    }

    @Override
    public String writeTo(Request request) {
        switch (request.getType()) {
            case TRANSACTION:
                return from((TransactionRequest) request);
            case TRANSACTION_QUERY:
                return from((TransactionQueryRequest) request);
            case TRANSACTION_NOTIFICATION:
                return from((TransactionNotificationRequest) request);
            default:
                throw new UnsupportedOperationException("Unsupported request type was used!");
        }
    }

    @Override
    public Response readFrom(String response, Request request) {
        switch (request.getType()) {
            case TRANSACTION:
                return toTransactionResponse(response, request);
            case TRANSACTION_QUERY:
                return toTransactionQueryResponse(response, request);
            case TRANSACTION_NOTIFICATION:
                return toTransactionNotificationResponse(response, request);
            default:
                throw new UnsupportedOperationException("Unsupported request type was used!");
        }
    }

    protected String from(TransactionRequest request) {
        throw new UnsupportedOperationException("Transaction is not supported");
    }

    protected String from(TransactionQueryRequest request) {
        throw new UnsupportedOperationException("Transaction query is not supported");
    }

    protected String from(TransactionNotificationRequest request) {
        throw new UnsupportedOperationException("Transaction notification is not supported");
    }

    protected TransactionResponse toTransactionResponse(String content, Request request) {
        throw new UnsupportedOperationException("Transaction is not supported");
    }

    protected TransactionQueryResponse toTransactionQueryResponse(String content, Request request) {
        throw new UnsupportedOperationException("Transaction query is not supported");
    }

    protected TransactionNotificationResponse toTransactionNotificationResponse(String content, Request request) {
        throw new UnsupportedOperationException("Transaction notification is not supported");
    }
}
