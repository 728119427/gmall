package com.atguigu.gmall.product.config;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.rabbit.constant.MqConst;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductRabbitConfig {

    /**
     * 商品上架消息监听队列
     * @return
     */
    @Bean("ProductQueue1")
    public Queue queue1(){
        return QueueBuilder.durable(MqConst.QUEUE_GOODS_UPPER).build();
    }

    /**
     * 商品下架消息监听队列
     * @return
     */
    @Bean("ProductQueue2")
    public Queue queue2(){
        return QueueBuilder.durable(MqConst.QUEUE_GOODS_LOWER).build();
    }

    /**
     * 商品上下架消息交换机
     * @return
     */
    @Bean("productExchange")
    public Exchange exchange(){
        return ExchangeBuilder.directExchange(MqConst.EXCHANGE_DIRECT_GOODS).build();
    }

    /**
     * 商品上架队列和交换机绑定
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding binding1(@Qualifier("ProductQueue1")Queue queue,@Qualifier("productExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(MqConst.ROUTING_GOODS_UPPER).noargs();
    }

    /**
     * 商品下架队列和交换机绑定
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding binding2(@Qualifier("ProductQueue2")Queue queue,@Qualifier("productExchange") Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(MqConst.ROUTING_GOODS_LOWER).noargs();
    }
}
