package com.atguigu.gmall.mq.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitReturn implements RabbitTemplate.ReturnCallback {
    /**
     * Returned message callback.
     *
     * @param message    the returned message.
     * @param replyCode  the reply code.
     * @param replyText  the reply text.
     * @param exchange   the exchange.
     * @param routingKey the routing key.
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("消息内容："+new String(message.getBody()));
        System.out.println("效应码："+replyCode);
        System.out.println("异常内容："+replyText);
        System.out.println("交换机："+exchange);
        System.out.println("routingKey:"+routingKey);
    }
}
