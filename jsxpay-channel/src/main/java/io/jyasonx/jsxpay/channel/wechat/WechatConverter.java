package io.jyasonx.jsxpay.channel.wechat;

import com.google.common.base.Strings;
import com.thoughtworks.xstream.XStream;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;
import io.jyasonx.jsxpay.channel.AbstractConverter;
import io.jyasonx.jsxpay.channel.Request;
import io.jyasonx.jsxpay.channel.ThirdpartyException;
import io.jyasonx.jsxpay.channel.Transaction;
import io.jyasonx.jsxpay.channel.bean.TransactionQueryRequest;
import io.jyasonx.jsxpay.channel.bean.TransactionQueryResponse;
import io.jyasonx.jsxpay.channel.bean.TransactionRequest;
import io.jyasonx.jsxpay.channel.bean.TransactionResponse;
import io.jyasonx.jsxpay.channel.wechat.mapper.ResponseMapper;
import io.jyasonx.jsxpay.common.TransactionStatus;
import io.jyasonx.jsxpay.util.DateUtils;
import io.jyasonx.jsxpay.util.IdUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class WechatConverter extends AbstractConverter {

    private static final String TEMPLATE_ATTRIBUTE_REQUEST = "request";

    protected static final String CODE_SUCCESS = "SUCCESS";
    private static final String CODE_ORDER_NOT_EXIST = "ORDERNOTEXIST";
    private static final String CODES_FAILED = "REFUND|CLOSED|REVOKED";

    private final Template transactionTemplate;
    private final Template transactionQueryTemplate;
    private final XStream stream;

    public WechatConverter() {
        Configuration configuration = FREEMARKER_HELPER.getConfiguration();
        try {
            transactionTemplate = configuration.getTemplate("WECHAT_Transaction.ftl");
            transactionQueryTemplate = configuration.getTemplate("WECHAT_TransactionQuery.ftl");
        } catch (IOException ex) {
            throw new ThirdpartyException("Failed to initialize templates.", ex);
        }

        stream = new XStream();
        XStream.setupDefaultSecurity(stream);
        stream.allowTypes(new Class[]{ResponseMapper.class});
        stream.processAnnotations(ResponseMapper.class);
        stream.ignoreUnknownElements();
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
        ResponseMapper mapper = (ResponseMapper) stream.fromXML(content);
        TransactionResponse response = new TransactionResponse();
        response.setCode(mapper.getCode());
        response.setMessage(mapper.getMessage());
        String transactionCode = CODE_SUCCESS.equals(mapper.getResultCode())
                ? mapper.getResultCode() : mapper.getErrorCode();
        TransactionStatus status = CODE_SUCCESS.equals(mapper.getResultCode())
                ? TransactionStatus.PROCESSING : TransactionStatus.FAILED;
        response.setTransaction(Transaction.builder()
                .status(status)
                .settlementDate(Objects.nonNull(mapper.getFinishedTime())
                        ? LocalDate.parse(mapper.getFinishedTime(), DateUtils.DATE_TIME) : null)
                .code(transactionCode)
                .message(mapper.getErrorMessage())
                .thirdpartyPrepayNo(mapper.getPrepayId())
                .codeUrl(mapper.getCodeUrl())
                .build());
        return response;
    }

    @Override
    protected String from(TransactionQueryRequest request) {
        try {
            BeansWrapper wrapper = new BeansWrapperBuilder(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).build();
            TemplateHashModel templateHashModel = wrapper.getStaticModels();

            Map<String, Object> data = new HashMap<>();
            data.put(TEMPLATE_ATTRIBUTE_REQUEST, request);
            data.put(IdUtils.class.getSimpleName(), templateHashModel.get(IdUtils.class.getName()));
            return render(transactionQueryTemplate, data);
        } catch (TemplateModelException ex) {
            throw new ThirdpartyException(ex.getMessage(), ex);
        }
    }

    @Override
    protected TransactionQueryResponse toTransactionQueryResponse(String content, Request request) {
        ResponseMapper mapper = (ResponseMapper) stream.fromXML(content);
        TransactionQueryResponse response = new TransactionQueryResponse();
        response.setCode(mapper.getCode());
        response.setMessage(mapper.getMessage());

        TransactionStatus status;
        String code = Strings.isNullOrEmpty(mapper.getErrorCode())
                ? mapper.getResultCode()
                : mapper.getErrorCode();
        String message = mapper.getErrorMessage();

        if (CODE_SUCCESS.equals(mapper.getCode()) || CODE_SUCCESS.endsWith(mapper.getResultCode())) {
            if (CODE_SUCCESS.equals(mapper.getTradeState())) {
                status = TransactionStatus.SUCCEED;
            } else if (CODE_ORDER_NOT_EXIST.equals(mapper.getErrorCode())) {
                status = TransactionStatus.FAILED;
            } else if (CODES_FAILED.contains(mapper.getTradeState())) {
                status = TransactionStatus.FAILED;
            } else {
                status = TransactionStatus.PROCESSING;
            }
        } else {
            status = TransactionStatus.PROCESSING;
        }
        response.getTransactions().add(Transaction.builder()
                .status(status)
                .settlementDate(Objects.nonNull(mapper.getFinishedTime())
                        ? LocalDate.parse(mapper.getFinishedTime(), DateUtils.DATE_TIME) : null)
                .code(code)
                .message(message)
                .build());
        return response;
    }
}
