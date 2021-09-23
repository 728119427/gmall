package com.atguigu.gmall.mq.listener;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class RabbitConsumer {

    @RabbitListener(queues ="java0223_queue")
    public void listen(Message message, Channel channel) throws Exception {
        System.out.println("消息内容："+new String(message.getBody()));
        //获取确认标识
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            //模拟错误
            int i = 10/0;
            //手动确认
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {
            //打印异常消息
            e.printStackTrace();
            //是不是重新被发送的消息
            Boolean redelivered = message.getMessageProperties().getRedelivered();
            //如果是再次发送的消息还不能消费则不再重新放入队列当中
            if(redelivered){
                //将消息保存到日志当中
                log.error(JSONObject.toJSONString(message));
                //抛弃消息
                channel.basicNack(deliveryTag,false,false);
            }
            //  拒绝消费
                channel.basicReject(deliveryTag,true);


        }
    }


}
