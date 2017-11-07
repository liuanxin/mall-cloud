package com.github.order.service;

import com.github.common.page.PageInfo;
import com.github.common.page.Pages;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单模块的接口实现类
 *
 * @author https://github.com/liuanxin
 */
@RestController
public class OrderService implements OrderInterface {
    
    @Override
    public PageInfo demo(String xx, Integer page, Integer limit) {
        return Pages.returnList(null);
    }
}
