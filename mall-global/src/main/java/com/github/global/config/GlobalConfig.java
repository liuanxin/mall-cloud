package com.github.global.config;

import com.github.common.util.ApplicationContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalConfig {

    @Bean
    public ApplicationContextUtil setupApplicationContext() {
        return new ApplicationContextUtil();
    }
}
