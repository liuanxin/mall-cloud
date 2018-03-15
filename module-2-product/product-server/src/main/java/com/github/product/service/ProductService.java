package com.github.product.service;

import com.github.common.json.JsonResult;
import com.github.common.page.PageInfo;
import com.github.common.page.Pages;
import com.github.common.util.LogUtil;
import com.github.global.model.Develop;
import com.github.liuanxin.api.annotation.ApiGroup;
import com.github.liuanxin.api.annotation.ApiIgnore;
import com.github.liuanxin.api.annotation.ApiMethod;
import com.github.liuanxin.api.annotation.ApiParam;
import com.github.product.constant.ProductConst;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品模块的接口实现类
 *
 * @author https://github.com/liuanxin
 */
@ApiGroup({ ProductConst.MODULE_INFO })
@RestController
public class ProductService implements ProductInterface {
    
    @ApiMethod(title = "商品测试接口", develop = Develop.PRODUCT)
    @Override
    public PageInfo demo(String xx, 
                         @ApiParam(desc = "当前页数") Integer page,
                         @ApiParam(desc = "每页条数") Integer limit) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug("调用实现类" + xx + ", page:" + page + ", limit:" + limit);
        }
        return Pages.returnPage(null);
    }

    @ApiIgnore
    @GetMapping("/")
    public JsonResult index() {
        return JsonResult.success("product module");
    }
}
