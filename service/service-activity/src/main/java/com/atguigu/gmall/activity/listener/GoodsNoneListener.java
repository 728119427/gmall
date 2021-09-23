package com.atguigu.gmall.activity.listener;

import com.atguigu.gmall.activity.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * 监听redis中商品数量为0的消息，更新数据库
 */
@Component
@Slf4j
public class GoodsNoneListener {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;


    @RabbitListener(queues = MqConst.QUEUE_STORE_NONE)
    public void listenNone(Message message, Channel channel){
        //获取数据
        String goodsId = new String(message.getBody());
        //根据id查找商品
        SeckillGoods seckillGoods = seckillGoodsMapper.selectById(goodsId);
        //解决幂等性问题
        if(seckillGoods.getStockCount()==0){
            return;
        }
        //更新秒杀商品状态
        seckillGoods.setStockCount(0);
        seckillGoodsMapper.updateById(seckillGoods);

        //手动确认消费
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (IOException e) {
            //打印日志
            e.printStackTrace();
            //记录日志
            log.error("发生异常，异常信息："+e.getMessage());
            try {
                if(message.getMessageProperties().isRedelivered()){
                    //记录消费失败信息
                    log.error("同步数据到数据库失败，商品id为："+goodsId);
                    //二次消费该消息则抛弃
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(),false);
                    return;
                }
                //将消息放回队列，继续消费
                channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            } catch (Exception ex) {
                log.error("同步数据到数据库失败，商品id为："+goodsId);
                throw new GmallException(ResultCodeEnum.valueOf(ex.getMessage()));
            }
        }


    }

}
