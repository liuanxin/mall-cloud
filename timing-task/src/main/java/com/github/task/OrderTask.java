package com.github.task;

import com.github.common.util.LogUtil;
import com.github.order.client.OrderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** 订单相关的定时任务 */
@Component
public class OrderTask {

    @Autowired
    private OrderClient orderClient;

    /** 每分钟 --> 取消下单已经超过了 24 小时的订单 */
    @Scheduled(cron = "0 */1 * * * *")
    public void cancelOrder() {
        LogUtil.recordTime();
        try {
            cancel();
        } finally {
            LogUtil.unbind();
        }
    }
    private void cancel() {
        int cancelCount = 0;

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            // ignore
        }

        // cancelCount = orderClient.yyy();
        if (LogUtil.ROOT_LOG.isInfoEnabled()) {
            LogUtil.ROOT_LOG.info("共取消 {} 笔订单", cancelCount);
        }
    }
}
