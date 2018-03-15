package com.github.user.service;

import com.github.common.json.JsonResult;
import com.github.common.page.PageInfo;
import com.github.common.page.Pages;
import com.github.common.util.LogUtil;
import com.github.global.model.Develop;
import com.github.liuanxin.api.annotation.ApiGroup;
import com.github.liuanxin.api.annotation.ApiIgnore;
import com.github.liuanxin.api.annotation.ApiMethod;
import com.github.liuanxin.api.annotation.ApiParam;
import com.github.user.constant.UserConst;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户模块的接口实现类
 *
 * @author https://github.com/liuanxin
 */
@ApiGroup({ UserConst.MODULE_INFO })
@RestController
public class UserService implements UserInterface {
    
    @ApiMethod(title = "用户测试接口", develop = Develop.USER)
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
        return JsonResult.success("user module");
    }
}
