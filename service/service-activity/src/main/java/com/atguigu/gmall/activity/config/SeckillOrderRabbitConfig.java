package com.atguigu.gmall.activity.config;

import com.atguigu.gmall.rabbit.constant.MqConst;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 取消秒杀超时订单的mq配置类
 */
@Configuration
public class SeckillOrderRabbitConfig {

    /**
     * 声明死信队列
     * @return
     */
    @Bean("secKillOrderQueue1")
    public Queue queue1(){
        return QueueBuilder.durable(MqConst.QUEUE_SECKILL_ORDER_CANCEL_DEAD)
                            .withArgument("x-dead-letter-exchange",MqConst.EXCHANGE_DIRECT_SECKILL_ORDER_CANCEL)
                            .withArgument("x-dead-letter-routing-key",MqConst.ROUTING_SECKILL_ORDER_CANCEL)
                            .build();
    }

    /**
     * 声明正常队列
     * @return
     */
    @Bean("secKillOrderQueue2")
    public Queue queue2(){
        return QueueBuilder.durable(MqConst.QUEUE_SECKILL_ORDER_CANCEL).build();
    }

    /**
     * 声明与死信队列绑定的交换机
     * @return
     */
    @Bean("secKillOrderExchange1")
    public Exchange exchange1(){
        return ExchangeBuilder.directExchange(MqConst.EXCHANGE_DIRECT_SECKILL_ORDER_CANCEL_DEAD).build();
    }

    /**
     * 声明与普通队列绑定的交换机
     * @return
     */
    @Bean("secKillOrderExchange2")
    public Exchange exchange2(){
        return ExchangeBuilder.directExchange(MqConst.EXCHANGE_DIRECT_SECKILL_ORDER_CANCEL).build();
    }

    /**
     * 绑定死信队列与交换机
     * @param queue
     * @param exchange
     * @return
     */
    @Bean("seckillCancelOrderBinding1")
    public Binding binding1(@Qualifier("secKillOrderQueue1") Queue queue, @Qualifier("secKillOrderExchange1") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(MqConst.ROUTING_SECKILL_ORDER_CANCEL_DEAD).noargs();
    }


    /**
     * 绑定普通队列与交换机
     * @param queue
     * @param exchange
     * @return
     */
    @Bean("seckillCancelOrderBinding2")
    public Binding binding2(@Qualifier("secKillOrderQueue2") Queue queue, @Qualifier("secKillOrderExchange2") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(MqConst.ROUTING_SECKILL_ORDER_CANCEL).noargs();
    }
}
