package io.jyasonx.jsxpay.channel;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;

@Slf4j
public class FreeMarkerHelper {

    @Getter
    private volatile Configuration configuration;

    public FreeMarkerHelper(String... templatePaths) {
        configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        // TODO move utf-8 to util module
        configuration.setDefaultEncoding("UTF-8");
        MultiTemplateLoader loader = new MultiTemplateLoader(
                Arrays.stream(templatePaths)
                        .map(it -> new ClassTemplateLoader(getClass(), it))
                        .toArray(ClassTemplateLoader[]::new));
        configuration.setTemplateLoader(loader);
        try {
            configuration.setSetting(Configuration.TEMPLATE_UPDATE_DELAY_KEY_SNAKE_CASE,
                    Integer.toString(Integer.MAX_VALUE));
        } catch (TemplateException ex) {
            log.error("Failed to set cache expiration time for freemarker configuration...", ex);
        }

        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public String render(Template template, Map data) {
        try {
            StringWriter writer = new StringWriter();
            template.process(data, writer);
            return writer.toString();
        } catch (TemplateException | IOException ex) {
            String name = template.getName();
            log.error("Failed to render the template - " + name, ex);
            throw new RuntimeException("Error rendering the template - " + name, ex);
        }
    }


}
