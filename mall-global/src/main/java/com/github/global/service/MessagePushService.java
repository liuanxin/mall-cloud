package com.github.global.service;

import com.github.global.constant.MessageQueueConst;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Queue;

/** 向 mq 发送数据 */
@Configuration
@ConditionalOnClass({ JmsTemplate.class, Queue.class, ActiveMQQueue.class})
public class MessagePushService {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void submitSimple(String simpleInfo) {
        jmsTemplate.convertAndSend(new ActiveMQQueue(MessageQueueConst.SIMPLE_MQ_NAME), simpleInfo);
    }
}
