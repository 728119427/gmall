package com.atguigu.gmall.order.config;

import com.atguigu.gmall.rabbit.constant.MqConst;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 关闭交易的队列与交换机配置类
 */
@Configuration
public class ClosePayRabbitConfig {

    /**
     * 声明接收关闭交易消息的队列
     * @return
     */
    @Bean("closePayQueue")
    public Queue queue(){
        return QueueBuilder.durable(MqConst.QUEUE_PAYMENT_CLOSE).build();
    }

    /**
     * 声明交换机
     * @return
     */
    @Bean("closePayExchange")
    public Exchange exchange(){
        return ExchangeBuilder.directExchange(MqConst.EXCHANGE_DIRECT_PAYMENT_CLOSE).build();
    }

    /**
     * 绑定队列与交换机
     * @param queue
     * @param exchange
     * @return
     */
    @Bean("closePayBinding")
    public Binding binding(@Qualifier("closePayQueue") Queue queue,@Qualifier("closePayExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(MqConst.ROUTING_PAYMENT_CLOSE).noargs();
    }

}
