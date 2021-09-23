package com.atguigu.gmall.mq.controller;

import com.atguigu.gmall.mq.config.RabbirConfirm;
import com.atguigu.gmall.mq.config.RabbitReturn;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mq")
public class SendController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitReturn rabbitReturn;
    @Autowired
    private RabbirConfirm rabbirConfirm;

    @GetMapping("/send")
    public String send(){
        //消息的可靠性投递设置
        rabbitTemplate.setReturnCallback(rabbitReturn);
        rabbitTemplate.setConfirmCallback(rabbirConfirm);
        rabbitTemplate.convertAndSend("java0223_topicExchange","java.haha","测试消息发送1");
        return "success";
    }
}
