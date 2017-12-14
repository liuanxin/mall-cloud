package com.github.user.config;

import com.github.global.model.Develop;
import com.github.liuanxin.api.annotation.EnableApiInfo;
import com.github.liuanxin.api.model.DocumentCopyright;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用户模块里需要放入 spring 上下文中的 bean
 *
 * @author https://github.com/liuanxin
 */
@Configuration
@EnableApiInfo
public class UserConfig {

    @Value("${online:false}")
    private boolean online;

    @Bean
    public DocumentCopyright urlCopyright() {
        return new DocumentCopyright()
                .setContact(Develop.CONTACT)
                .setTeam(Develop.TEAM)
                .setVersion(UserConst.MODULE_VERSION)
                .setOnline(online);
    }
}
