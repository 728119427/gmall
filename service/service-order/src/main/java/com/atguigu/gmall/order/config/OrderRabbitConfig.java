package com.atguigu.gmall.order.config;

import com.atguigu.gmall.rabbit.constant.MqConst;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderRabbitConfig {

    /**
     * 声明死信队列
     * @return
     */
    @Bean("OrderQueue1")
    public Queue queue1(){
        return QueueBuilder.durable(MqConst.QUEUE_ORDER_CANCEL_DEAD)
                            .withArgument("x-dead-letter-exchange",MqConst.EXCHANGE_DIRECT_ORDER_CANCEL)
                            .withArgument("x-dead-letter-routing-key",MqConst.ROUTING_ORDER_CANCEL)
                            .build();
    }

    /**
     * 声明正常队列
     * @return
     */
    @Bean("OrderQueue2")
    public Queue queue2(){
        return QueueBuilder.durable(MqConst.QUEUE_ORDER_CANCEL).build();
    }

    /**
     * 声明与死信队列绑定的交换机
     * @return
     */
    @Bean("OrderExchange1")
    public Exchange exchange1(){
        return ExchangeBuilder.directExchange(MqConst.EXCHANGE_DIRECT_ORDER_CANCEL_DEAD).build();
    }

    /**
     * 声明与普通队列绑定的交换机
     * @return
     */
    @Bean("OrderExchange2")
    public Exchange exchange2(){
        return ExchangeBuilder.directExchange(MqConst.EXCHANGE_DIRECT_ORDER_CANCEL).build();
    }

    /**
     * 绑定死信队列与交换机
     * @param queue
     * @param exchange
     * @return
     */
    @Bean("productBinding1")
    public Binding binding1(@Qualifier("OrderQueue1") Queue queue, @Qualifier("OrderExchange1") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(MqConst.ROUTING_ORDER_CANCEL_DEAD).noargs();
    }


    /**
     * 绑定普通队列与交换机
     * @param queue
     * @param exchange
     * @return
     */
    @Bean("productBinding2")
    public Binding binding2(@Qualifier("OrderQueue2") Queue queue, @Qualifier("OrderExchange2") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(MqConst.ROUTING_ORDER_CANCEL).noargs();
    }
}
