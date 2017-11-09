package com.github.common.service;

import com.github.common.page.PageInfo;
import com.github.common.page.Pages;
import com.github.common.util.LogUtil;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公共服务模块的接口实现类
 *
 * @author https://github.com/liuanxin
 */
@RestController
public class CommonService implements CommonInterface {
    
    @Override
    public PageInfo demo(String xx, Integer page, Integer limit) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug("调用实现类");
        }
        return Pages.returnList(null);
    }
}
