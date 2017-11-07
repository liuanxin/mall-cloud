package com.github.common.client;

import com.github.common.service.CommonInterface;
import com.github.common.config.CommonConst;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * 公共服务相关的调用接口
 * 
 * @author https://github.com/liuanxin
 */
@FeignClient(value = CommonConst.MODULE_NAME)
public interface CommonClient extends CommonInterface {
}
