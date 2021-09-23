package com.atguigu.gmall.pay.listener;

import com.atguigu.gmall.pay.service.PaymentService;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClosePayListener {
    @Autowired
    private PaymentService paymentService;

    @RabbitListener(queues = MqConst.QUEUE_PAYMENT_CLOSE)
    public void listenClosePayMsg(Message message, Channel channel){
        //获取消息
        String orderId = new String(message.getBody());
        paymentService.closeOrder(Long.parseLong(orderId));
    }
}
