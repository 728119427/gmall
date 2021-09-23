package com.atguigu.gmall.activity.listener;

import com.atguigu.gmall.activity.service.SeckillOrderService;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class TimeoutOrderListener {
    @Autowired
    private SeckillOrderService seckillOrderService;

    @RabbitListener(queues = MqConst.QUEUE_SECKILL_ORDER_CANCEL)
    public void listenTimeoutMsg(Message message, Channel channel){
        //获取消息
        String username = new String (message.getBody());
        try {
            seckillOrderService.cancelSecKillOrder(username);
            //手动确认消费
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            //打印日志
            e.printStackTrace();
            //记录日志
            log.error("发生异常，异常信息："+e.getMessage());
            try {
                if(message.getMessageProperties().isRedelivered()){
                    //记录消费失败信息
                    log.error("取消订单失败，用户id为："+username);
                    //二次消费该消息则抛弃
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
                    return;
                }
                //将消息放回队列，继续消费
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            } catch (Exception ex) {
                log.error("取消订单失败，用户id为："+username);
                throw new GmallException(ResultCodeEnum.valueOf(ex.getMessage()));
            }
        }


    }

}
