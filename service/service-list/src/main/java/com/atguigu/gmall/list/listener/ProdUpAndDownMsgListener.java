package com.atguigu.gmall.list.listener;

import com.atguigu.gmall.list.service.GoodsService;
import com.atguigu.gmall.rabbit.constant.MqConst;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProdUpAndDownMsgListener {
    @Autowired
    private GoodsService goodsService;

    /**
     * 监听商品上架消息
     * @param msg
     */
    @RabbitListener(queues = MqConst.QUEUE_GOODS_UPPER)
    public void listenOnSaleMessage(String msg){
        goodsService.upperGoods(Long.parseLong(msg));
    }

    /**
     * 监听商品下架消息
     * @param msg
     */
    @RabbitListener(queues = MqConst.QUEUE_GOODS_LOWER)
    public void listenCancelSaleMessage(String msg){
        goodsService.lowerGoods(Long.parseLong(msg));
    }
}
