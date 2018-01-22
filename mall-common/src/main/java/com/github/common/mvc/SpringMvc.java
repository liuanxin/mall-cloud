package com.github.common.mvc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.github.common.converter.*;
import com.github.common.json.JsonUtil;
import com.github.common.page.Page;
import com.github.common.util.A;
import com.github.common.util.LogUtil;
import com.github.common.util.RequestUtils;
import com.github.common.util.U;
import org.springframework.core.MethodParameter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

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

            if (LogUtil.ROOT_LOG.isInfoEnabled()) {
                String jsonp = null;
                Object render = object;
                if (object instanceof MappingJacksonValue) {
                    render = ((MappingJacksonValue) object).getValue();
                    jsonp = ((MappingJacksonValue) object).getJsonpFunction();
                }
                String toRender = JsonUtil.toJson(render);
                if (U.isNotBlank(jsonp)) {
                    toRender = "/**/" + jsonp + "(" + toRender + ");";
                }
                LogUtil.ROOT_LOG.info("return json : {}", toRender);
            }
        }
    }

    public static void handlerArgument(List<HandlerMethodArgumentResolver> argumentResolvers) {
        // 参数是 Page 对象时
        argumentResolvers.add(new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return Page.class.isAssignableFrom(parameter.getParameterType());
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest request, WebDataBinderFactory factory) throws Exception {
                Page page = new Page(request.getParameter(Page.GLOBAL_PAGE), request.getParameter(Page.GLOBAL_LIMIT));
                page.setWasMobile(RequestUtils.isMobileRequest());
                return page;
            }
        });
        // 参数是 page 名称时
        argumentResolvers.add(new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return Page.GLOBAL_PAGE.equals(parameter.getParameterName());
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest request, WebDataBinderFactory factory) throws Exception {
                return Page.handlerPage(request.getParameter(Page.GLOBAL_PAGE));
            }
        });
        // 参数是 limit 名称时
        argumentResolvers.add(new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return Page.GLOBAL_LIMIT.equals(parameter.getParameterName());
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest request, WebDataBinderFactory factory) throws Exception {
                return Page.handlerLimit(request.getParameter(Page.GLOBAL_LIMIT));
            }
        });
    }
}
