package com.github.queue.hystrix;

import com.github.common.page.PageInfo;
import com.github.common.page.Pages;
import com.github.common.util.LogUtil;
import com.github.queue.client.QueueClient;
import org.springframework.stereotype.Component;

/**
 * 消息队列相关的断路器
 */
@Component
public class QueueClientFallback implements QueueClient {

    @Override
    public PageInfo demo(String xx, Integer page, Integer limit) {
        if (LogUtil.ROOT_LOG.isDebugEnabled()) {
            LogUtil.ROOT_LOG.debug("调用断路器");
        }
        return Pages.returnPage(null);
    }
}
