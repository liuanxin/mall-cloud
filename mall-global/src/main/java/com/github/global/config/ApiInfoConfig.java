package com.github.global.config;

import com.github.global.model.Develop;
import com.github.liuanxin.api.annotation.EnableApiInfo;
import com.github.liuanxin.api.model.DocumentCopyright;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;

@Configuration
@EnableApiInfo
@ConditionalOnClass({ Servlet.class, DocumentCopyright.class })
public class ApiInfoConfig {

    @Value("${online:false}")
    private boolean online;

    @Bean
    public DocumentCopyright urlCopyright() {
        return new DocumentCopyright()
                .setTitle(Develop.TITLE)
                .setContact(Develop.CONTACT)
                .setCopyright(Develop.COPYRIGHT)
                .setVersion(Develop.VERSION)
                .setOnline(online)
                .setIgnoreUrlSet(Sets.newHashSet("/error"));
    }
}
