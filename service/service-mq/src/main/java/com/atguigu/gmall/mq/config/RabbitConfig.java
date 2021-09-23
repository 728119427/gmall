package com.atguigu.gmall.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    /**
     * 声明队列
     * @return
     */
    @Bean("myQueue")
    public Queue myQueue(){
        return QueueBuilder.durable("java0223_queue").build();
    }

    /**
     * 声明交换机
     * @return
     */
    @Bean("myExchange")
    public Exchange exchange(){
        return ExchangeBuilder.topicExchange("java0223_topicExchange").build();
    }

    /**
     * 绑定队列和交换机
     */
    @Bean
    public Binding binding(@Qualifier("myQueue") Queue queue, @Qualifier("myExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("java.#").noargs();
    }
}
