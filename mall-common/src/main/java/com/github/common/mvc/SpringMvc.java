package com.github.common.mvc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.github.common.converter.*;
import com.github.common.json.JsonUtil;
import com.github.common.util.LogUtil;
import com.github.common.converter.*;
import com.github.common.util.A;
import com.github.common.util.U;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class SpringMvc {

    public static void handlerFormatter(FormatterRegistry registry) {
        registry.addConverter(new StringTrimAndEscapeConverter());
        registry.addConverterFactory(new StringToNumberConverter());
        registry.addConverterFactory(new StringToEnumConverter());
        registry.addConverter(new String2DateConverter());
        registry.addConverter(new String2MoneyConverter());
    }

    public static void handlerConvert(List<HttpMessageConverter<?>> converters) {
        if (A.isNotEmpty(converters)) {
            converters.removeIf(converter -> converter instanceof StringHttpMessageConverter
                    || converter instanceof MappingJackson2HttpMessageConverter);
        }
        converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8));
        converters.add(new CustomizeJacksonConverter());
    }

    public static class CustomizeJacksonConverter extends MappingJackson2HttpMessageConverter {
        CustomizeJacksonConverter() { super(JsonUtil.RENDER); }
        @Override
        protected void writeSuffix(JsonGenerator generator, Object object) throws IOException {
            super.writeSuffix(generator, object);

            String jsonp = null;
            Object render = object;
            if (object instanceof MappingJacksonValue) {
                render = ((MappingJacksonValue) object).getValue();
                jsonp = ((MappingJacksonValue) object).getJsonpFunction();
            }
            if (LogUtil.ROOT_LOG.isDebugEnabled()) {
                String toRender = JsonUtil.toJson(render);
                if (U.isNotBlank(jsonp)) {
                    toRender = "/**/" + jsonp + "(" + toRender + ");";
                }
                LogUtil.ROOT_LOG.debug("return: {}", toRender);
            }
        }
    }
}
