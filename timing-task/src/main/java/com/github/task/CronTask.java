package com.github.task;

import com.github.common.util.LogUtil;
import org.springframework.scheduling.annotation.Scheduled;

/** 定时任务 --> 示例 */
// @Component
public class CronTask {

    /** 默认是每分钟运行一次 */
    private static final String DEFAULT_CRON = "0 */1 * * * *";

    // @Autowired
    // private OrderClient orderClient;

    /** 每分钟 --> 取消下单已经超过了 24 小时的订单 */
    @Scheduled(cron = DEFAULT_CRON)
    public void cancelOrder() {
        LogUtil.recordTime();
        try {
            // int cancelCount = orderClient.xxx();
            // if (LogUtil.ROOT_LOG.isInfoEnabled()) {
            //     LogUtil.ROOT_LOG.info("共取消 {} 笔订单", cancelCount);
            // }
        } catch (Exception e) {
            if (LogUtil.ROOT_LOG.isErrorEnabled()) {
                LogUtil.ROOT_LOG.error("取消订单时异常", e);
            }
        } finally {
            LogUtil.unbind();
        }
    }
}
