package com.github.order.client;

import com.github.order.service.OrderInterface;
import com.github.order.config.OrderConst;
import com.github.order.hystrix.OrderFallback;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * 订单相关的调用接口
 * 
 * @author https://github.com/liuanxin
 */
@FeignClient(value = OrderConst.MODULE_NAME, fallback = OrderFallback.class)
public interface OrderClient extends OrderInterface {
}
