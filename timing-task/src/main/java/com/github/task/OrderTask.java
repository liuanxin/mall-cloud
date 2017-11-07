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

        int cancelCount = 0;
        try {
            // cancelCount = orderService.yyy();
        } catch (Exception e) {
            if (LogUtil.ROOT_LOG.isErrorEnabled()) {
                LogUtil.ROOT_LOG.error("取消订单时异常", e);
            }
        }

        if (LogUtil.ROOT_LOG.isInfoEnabled()) {
            LogUtil.ROOT_LOG.info("取消订单操作完成. 共取消 {} 笔订单", cancelCount);
        }
        LogUtil.unbind();
    }
}
