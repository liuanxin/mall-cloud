package com.github.common.service;

import com.github.common.page.PageInfo;
import com.github.common.page.Pages;
import com.github.common.util.LogUtil;
import com.github.global.model.Develop;
import com.github.liuanxin.api.annotation.ApiGroup;
import com.github.liuanxin.api.annotation.ApiMethod;
import com.github.liuanxin.api.annotation.ApiParam;
import com.github.common.config.CommonConst;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公共服务模块的接口实现类
 *
 * @author https://github.com/liuanxin
 */
@ApiGroup({ CommonConst.MODULE_INFO })
@RestController
public class CommonService implements CommonInterface {
    
    @ApiMethod(title = "公共服务测试接口", develop = Develop.COMMON)
    @Override
    public PageInfo demo(String xx, 
                         @ApiParam(desc = "当前页数") Integer page,
                         @ApiParam(desc = "每页条数") Integer limit) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug("调用实现类");
        }
        return Pages.returnList(null);
    }
}
