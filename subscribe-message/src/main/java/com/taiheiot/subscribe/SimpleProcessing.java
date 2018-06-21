package com.taiheiot.subscribe;

import com.github.common.util.LogUtil;
import com.github.global.constant.MessageQueueConst;
import com.github.order.client.OrderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

@Component
public class SimpleProcessing {

    @Autowired
    private OrderClient orderService;

    @JmsListener(destination = MessageQueueConst.SIMPLE_MQ_NAME)
    public void receiveMessage(final String message) throws JMSException {
        LogUtil.recordTime();
        try {
            handlerBusiness(message);
        } finally {
            if (LogUtil.ROOT_LOG.isInfoEnabled()) {
                LogUtil.ROOT_LOG.info("接收队列({})的数据({})并处理完成", MessageQueueConst.SIMPLE_MQ_NAME, message);
            }
            LogUtil.unbind();
        }
    }

    /** 操作具体的业务 */
    private void handlerBusiness(String message) {
        // int count = orderService.xxx(message);
        // if (LogUtil.ROOT_LOG.isInfoEnabled()) {
        //     LogUtil.ROOT_LOG.info("消费 mq 时操作了 {} 笔订单", count);
        // }
    }
}
