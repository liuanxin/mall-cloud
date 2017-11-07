package com.github.product.service;

import com.github.common.page.PageInfo;
import com.github.common.page.Pages;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品模块的接口实现类
 *
 * @author https://github.com/liuanxin
 */
@RestController
public class ProductService implements ProductInterface {
    
    @Override
    public PageInfo demo(String xx, Integer page, Integer limit) {
        return Pages.returnList(null);
    }
}
