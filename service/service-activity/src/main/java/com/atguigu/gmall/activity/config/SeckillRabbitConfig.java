package com.atguigu.gmall.activity.config;

import com.atguigu.gmall.rabbit.constant.MqConst;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeckillRabbitConfig {

    /**
     * 声明秒杀队列
     * @return
     */
    @Bean("seckillQueue")
    public Queue queue(){
        return QueueBuilder.durable(MqConst.QUEUE_SECKILL_USER).build();
    }

    /**
     * 声明交换机
     * @return
     */
    @Bean("seckillExchange")
    public Exchange exchange(){
        return ExchangeBuilder.directExchange(MqConst.EXCHANGE_DIRECT_SECKILL_USER).build();
    }

    /**
     * 绑定队列和交换机
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding binding(@Qualifier("seckillQueue") Queue queue,@Qualifier("seckillExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(MqConst.ROUTING_SECKILL_USER).noargs();
    }
}
