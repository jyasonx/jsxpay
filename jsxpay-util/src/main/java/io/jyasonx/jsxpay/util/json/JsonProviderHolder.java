package io.jyasonx.jsxpay.util.json;


import io.jyasonx.jsxpay.util.json.jackson.JacksonProvider;

/**
 * {@code JsonProviderHolder} hold {@code JsonProvider} for providing Json related
 * features/methods.
 */
public class JsonProviderHolder {

    private JsonProviderHolder() {

    }

    /**
     * Create the json provider of Jackson.
     */
    public static final JsonProvider JACKSON = new JacksonProvider();

}
