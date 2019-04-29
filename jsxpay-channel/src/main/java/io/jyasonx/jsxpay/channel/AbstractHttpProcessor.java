package io.jyasonx.jsxpay.channel;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.util.function.Function;

@Slf4j
public class AbstractHttpProcessor implements Processor {

    protected HttpClient httpClient;

    public AbstractHttpProcessor(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public <T extends Response> T execute(Request request) {
        throw new ThirdpartyException("Request generation is not expected by this processor");
    }

    @Override
    public <T extends Response> T handle(String notification, Request request) {
        throw new ThirdpartyException("Notification is not expected by this processor");
    }

    @Override
    public String from(Request request) {
        throw new ThirdpartyException("Notification is not expected by this processor");
    }

    @Override
    public <T extends Request> T handle(String notification, Request request, Function<Response, Request> function) {
        throw new ThirdpartyException("Notification is not expected by this processor");
    }

    protected HttpEntity doExecute(HttpRequestBase request) {
        try {
            return httpClient.execute(request).getEntity();
        } catch (IOException ex) {
            log.error("Failed to communicate with remote server due to an exception!", ex);
            throw new ThirdpartyException("Error connecting to remote server", ex);
        }
    }
}
