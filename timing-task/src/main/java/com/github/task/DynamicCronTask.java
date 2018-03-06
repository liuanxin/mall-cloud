package com.github.task;

import com.github.common.client.CommonClient;
import com.github.common.util.U;
import com.github.product.client.ProductClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;

/** 动态设置运行时间的定时任务 --> 示例 */
// @Component
public class DynamicCronTask implements SchedulingConfigurer {

    @Autowired
    private ProductClient productClient;

    @Autowired
    private CommonClient commonClient;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                // 操作具体的业务
                // productClient.cancel();
            }
        };

        Trigger trigger = new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                // 从数据库读取定时
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

