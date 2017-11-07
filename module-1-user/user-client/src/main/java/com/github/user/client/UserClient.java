package com.github.user.client;

import com.github.user.service.UserInterface;
import com.github.user.config.UserConst;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * 用户相关的调用接口
 * 
 * @author https://github.com/liuanxin
 */
@FeignClient(value = UserConst.MODULE_NAME)
public interface UserClient extends UserInterface {
}
