package com.github.product.client;

import com.github.product.service.ProductInterface;
import com.github.product.config.ProductConst;
import com.github.product.hystrix.ProductFallback;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * 商品相关的调用接口
 * 
 * @author https://github.com/liuanxin
 */
@FeignClient(value = ProductConst.MODULE_NAME, fallback = ProductFallback.class)
public interface ProductClient extends ProductInterface {
}
