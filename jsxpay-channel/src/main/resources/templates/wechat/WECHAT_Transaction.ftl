<#-- @ftlvariable name="request" type="io.jyasonx.jsxpay.channel.bean.TransactionRequest" -->
<#setting number_format="######0"/>
<#import "WECHAT_Macro.ftl" as macro>
<@macro.compress_single_line>
<?xml version="1.0" encoding="UTF-8"?>
<xml>
    <appid>wx2421b1c4370ec43b</appid>
    <mch_id>10000100</mch_id>
    <nonce_str>1add1a30ac87aa2db72f57a2375d8fec</nonce_str>
    <out_trade_no>1415659990</out_trade_no>
    <body>JSAPI支付测试</body>
    <detail><![CDATA[{ "goods_detail":[ { "goods_id":"iphone6s_16G", "wxpay_goods_id":"1001", "goods_name":"iPhone6s 16G", "quantity":1, "price":528800, "goods_category":"123456", "body":"苹果手机" }, { "goods_id":"iphone6s_32G", "wxpay_goods_id":"1002", "goods_name":"iPhone6s 32G", "quantity":1, "price":608800, "goods_category":"123789", "body":"苹果手机" } ] }]]></detail>
    <notify_url>${request.config.callbackUrl}</notify_url>
    <fee_type>CNY</fee_type>
    <total_fee>1</total_fee>
    <time_start>20190425183820</time_start>
    <time_expire>20190425213816</time_expire>
    <trade_type>NATIVE</trade_type>
    <product_id>20190425183816</product_id>
    <sign></sign>
</xml>
</@macro.compress_single_line>