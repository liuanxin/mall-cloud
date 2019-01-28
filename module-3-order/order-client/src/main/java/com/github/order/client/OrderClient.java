package com.github.order.client;

import com.github.order.constant.OrderConst;
import com.github.order.hystrix.OrderClientFallback;
import com.github.order.service.OrderService;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * 订单相关的调用接口
 *
 * @author https://github.com/liuanxin
 */
@FeignClient(value = OrderConst.MODULE_NAME, fallback = OrderClientFallback.class)
public interface OrderClient extends OrderService {
}
