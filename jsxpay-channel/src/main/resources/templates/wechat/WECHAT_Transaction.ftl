<#-- @ftlvariable name="request" type="io.jyasonx.jsxpay.channel.bean.TransactionRequest" -->
<#-- @ftlvariable name="IdUtils" type="io.jyasonx.jsxpay.util.IdUtils" -->
<#-- @ftlvariable name="DateUtils" type="io.jyasonx.jsxpay.util.DateUtils" -->
<#-- @ftlvariable name="DateTimeFormatter" type="java.time.format.DateTimeFormatter" -->
<#setting number_format="######0"/>
<#import "WECHAT_Macro.ftl" as macro>
<@macro.compress_single_line>
<?xml version="1.0" encoding="UTF-8"?>
<xml>
    <appid>${request.config.appId}</appid>
    <mch_id>${request.config.merchantNo}</mch_id>
    <nonce_str>${IdUtils.uuidWithoutDash()}</nonce_str>
    <out_trade_no>${request.transaction.channelSerialNo}</out_trade_no>
    <product_id>${request.transaction.channelSerialNo}</product_id>
    <sign_type>${request.config.signatureAlgorithm}</sign_type>
    <body>${request.transaction.description}</body>
    <detail>${request.transaction.description}</detail>
    <notify_url>${request.config.callbackUrl}</notify_url>
    <fee_type>CNY</fee_type>
    <total_fee>${request.transaction.amount * 100}</total_fee>
    <time_start>${request.createdTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))}</time_start>
    <time_expire>${request.transaction.expireTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))}</time_expire>
    <trade_type>NATIVE</trade_type>
    <sign></sign>
</xml>
</@macro.compress_single_line>