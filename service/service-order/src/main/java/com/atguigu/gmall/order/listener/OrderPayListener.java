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
import java.util.Map;

@Component
@Slf4j
public class OrderPayListener {
    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = MqConst.QUEUE_PAYMENT_PAY)
    public void listenPayMsg(Message message, Channel channel){
        try {
            //获取消息
            String msg = new String(message.getBody());
            //转换为map
            Map notifyMap = JSONObject.parseObject(msg, Map.class);
            if(notifyMap.get("return_code").equals("SUCCESS")){
                if(notifyMap.get("result_code").equals("SUCCESS")){
                    //更改订单状态
                    orderService.updateOrderStatus(notifyMap);
                }
            }
            //手动确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            //打印异常消息
            e.printStackTrace();
            //记录异常信息
            log.error("异常信息："+e.getMessage());
                try {
                    //判断是否二次消费
                    if(message.getMessageProperties().isRedelivered()){
                        //记录消费失败信息
                        log.error("消息消费失败，失败内容为： "+ JSONObject.toJSONString(message));
                        //抛弃消息
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
                    }else {
                        //不是二次消费则再次放入队列中
                        channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
                    }
                } catch (Exception ex) {
                    throw new GmallException(ResultCodeEnum.valueOf(ex.getMessage()));
                }


        }
    }
}
