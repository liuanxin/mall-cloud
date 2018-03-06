package com.github.task;

import com.github.common.util.LogUtil;
import com.github.common.util.U;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;

/** 动态设置运行时间的定时任务 --> 示例 */
// @Component
public class DynamicCronTask implements SchedulingConfigurer {

    // @Autowired
    // private ProductClient productClient;

    // @Autowired
    // private CommonClient commonClient;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                LogUtil.recordTime();
                try {
                    // 操作具体的业务
                    // int offlineCount = productClient.yyy();
                    // if (LogUtil.ROOT_LOG.isInfoEnabled()) {
                    //     LogUtil.ROOT_LOG.info("共下架 {} 个商品", offlineCount);
                    // }
                } catch (Exception e) {
                    if (LogUtil.ROOT_LOG.isErrorEnabled()) {
                        LogUtil.ROOT_LOG.error("下架商品时异常", e);
                    }
                } finally {
                    LogUtil.unbind();
                }
            }
        };

        Trigger trigger = new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                // 从数据库读取 cron 表达式
                String cron = ""; // commonClient.getAbcCron();
                if (U.isBlank(cron)) {
                    // 如果没有, 给一个默认值.
                    cron = "0 0 0/1 * * *";
                }
                return new CronTrigger(cron).nextExecutionTime(triggerContext);
            }
        };
        taskRegistrar.addTriggerTask(task, trigger);
    }
}

