package com.github.queue.service;

import com.github.common.json.JsonResult;
import com.github.common.page.PageInfo;
import com.github.common.page.Pages;
import com.github.common.util.LogUtil;
import com.github.queue.constant.QueueConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Queue;

/**
 * 消息队列模块的接口实现类
 *
 * @author https://github.com/liuanxin
 */
@RestController
public class QueueServiceImpl implements QueueInterface {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier(QueueConst.SIMPLE_MQ_NAME)
    private Queue simpleQueue;

    @Override
    public void submitSimple(String simpleInfo) {
        jmsTemplate.convertAndSend(simpleQueue, simpleInfo);
    }

    @GetMapping("/")
    public JsonResult index() {
        return JsonResult.success("Queue-module");
    }
}
