package com.atguigu.gmall.order.listener;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Slf4j
@Component
public class OrderCancelListener {

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = MqConst.QUEUE_ORDER_CANCEL)
    public void listenOrderMsg(Message message, Channel channel){
        //获取消息
        String msg = new String(message.getBody());
        try {
            //取消订单
            orderService.cancelOrder(Long.parseLong(msg));
            //确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                //记录异常信息
                //log.error(e.getMessage());
                //判断该消息是否二次被发送
                if(message.getMessageProperties().isRedelivered()){
                    //记录消息消费失败信息
                    log.error("消息消费失败，失败的消息内容为： "+JSONObject.toJSONString(message));
                    //抛弃该消息
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
                }else {
                    //非二次发送，拒绝消息，再次放入队列消费
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
                }
            } catch (Exception ex) {
                throw new GmallException(ResultCodeEnum.valueOf(ex.getMessage()));
            }
        }
    }
}
