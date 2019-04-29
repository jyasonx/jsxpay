package io.jyasonx.jsxpay.channel.wechat;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.*;
import io.jyasonx.jsxpay.channel.AbstractConverter;
import io.jyasonx.jsxpay.channel.Request;
import io.jyasonx.jsxpay.channel.ThirdpartyException;
import io.jyasonx.jsxpay.channel.bean.TransactionQueryRequest;
import io.jyasonx.jsxpay.channel.bean.TransactionQueryResponse;
import io.jyasonx.jsxpay.channel.bean.TransactionRequest;
import io.jyasonx.jsxpay.channel.bean.TransactionResponse;
import io.jyasonx.jsxpay.util.DateUtils;
import io.jyasonx.jsxpay.util.IdUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class WechatConverter extends AbstractConverter {

    private static final String TEMPLATE_ATTRIBUTE_REQUEST = "request";

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

        try {
            BeansWrapper wrapper = new BeansWrapperBuilder(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).build();
            TemplateHashModel templateHashModel = wrapper.getStaticModels();

            Map<String, Object> data = new HashMap<>();
            data.put(TEMPLATE_ATTRIBUTE_REQUEST, request);
            data.put(IdUtils.class.getSimpleName(), templateHashModel.get(IdUtils.class.getName()));
            data.put(DateUtils.class.getSimpleName(), templateHashModel.get(DateUtils.class.getName()));
            data.put(DateTimeFormatter.class.getSimpleName(), templateHashModel.get(DateTimeFormatter.class.getName()));
            return render(transactionTemplate, data);
        } catch (TemplateModelException ex) {
            throw new ThirdpartyException(ex.getMessage(), ex);
        }

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
