package com.atguigu.gmall.activity.config;

import com.atguigu.gmall.rabbit.constant.MqConst;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreNoneRabbitConfig {

    /**
     * 声明队列
     * @return
     */
    @Bean("storeNoneQueue")
    public Queue queue(){
    return QueueBuilder.durable(MqConst.QUEUE_STORE_NONE).build();
    }

    /**
     * 声明交换机
     * @return
     */
    @Bean("storeNoneExchange")
    public Exchange exchange(){
        return ExchangeBuilder.directExchange(MqConst.EXCHANGE_DIRECT_STORE_NONE).build();
    }

    /**
     * 绑定队列和交换机
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding binding(@Qualifier("storeNoneQueue") Queue queue,@Qualifier("storeNoneExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(MqConst.ROUTING_STORE_NONE).noargs();
    }
}
