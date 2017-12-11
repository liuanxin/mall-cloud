package com.github.config;

import com.github.liuanxin.api.annotation.EnableApiInfo;
import com.github.liuanxin.api.model.DocumentCopyright;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableApiInfo
public class ApiInfoConfig {

    @Bean
    public DocumentCopyright urlCopyright() {
        return new DocumentCopyright()
                .setContact("联系")
                .setTeam("团队")
                .setVersion("文档版本号");
    }
}
