package io.jyasonx.jsxpay.channel;

import java.util.function.Function;

public interface Processor {

    String NAME_SUFFIX = "Processor";

    <T extends Response> T execute(Request request);

    <T extends Response> T handle(String notification, Request request);

    String from(Request request);

    <T extends Request> T handle(String notification, Request request, Function<Response, Request> function);

}
