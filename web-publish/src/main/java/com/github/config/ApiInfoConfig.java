package com.github.config;

import com.github.common.AppVersion;
import com.github.liuanxin.api.annotation.EnableApiInfo;
import com.github.liuanxin.api.model.DocumentCopyright;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableApiInfo
public class ApiInfoConfig {

    @Bean
    public DocumentCopyright copyright() {
        return new DocumentCopyright()
                .setTitle("xxx project document api")
                .setContact("contact")
                .setTeam("team")
                //.setReturnRecordLevel(true)
                .setVersion(AppVersion.currentVersion());
    }
}
