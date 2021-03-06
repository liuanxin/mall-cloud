package com.github.queue.service;

import com.github.common.json.JsonResult;
import com.github.common.page.PageInfo;
import com.github.common.page.Pages;
import com.github.common.util.LogUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息队列模块的接口实现类
 */
@RestController
public class QueueServiceImpl implements QueueService {
    
    @Override
    public PageInfo demo(String xx, Integer page, Integer limit) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug("调用实现类" + xx + ", page:" + page + ", limit:" + limit);
        }
        return Pages.returnPage(null);
    }

    @GetMapping("/")
    public JsonResult index() {
        return JsonResult.success("Queue-module");
    }
}
