package io.jyasonx.jsxpay.channel;

public interface Converter {

    String writeTo(Request request);

    Response readFrom(String response, Request request);
}
