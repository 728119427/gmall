package com.atguigu.gmall.pay.config;

import com.atguigu.gmall.rabbit.constant.MqConst;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class PayRabbitConfig {

    /**
     * 声明支付队列
     * @return
     */
    @Bean("PayQueue")
    public Queue queue(){
        return QueueBuilder.durable(MqConst.QUEUE_PAYMENT_PAY).build();
    }

    /**
     * 声明交换机
     * @return
     */
    @Bean("payExchange")
    public Exchange exchange(){
        return ExchangeBuilder.directExchange(MqConst.EXCHANGE_DIRECT_PAYMENT_PAY).build();
    }

    /**
     * 绑定队列和交换机
     * @param queue
     * @param exchange
     * @return
     */
    @Bean("payBinding")
    public Binding binding(@Qualifier("PayQueue") Queue queue,@Qualifier("payExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(MqConst.ROUTING_PAYMENT_PAY).noargs();
    }
}
