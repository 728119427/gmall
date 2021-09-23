package com.atguigu.gmall.activity.listener;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.activity.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.activity.pojo.SeckillOrder;
import com.atguigu.gmall.activity.pojo.UserRecord;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.rabbit.config.RabbitService;
import com.atguigu.gmall.rabbit.constant.MqConst;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class SecKillAddOrderListener {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(queues = MqConst.QUEUE_SECKILL_USER)
    public void listenSeckillMsg(String msg){
        //获取消息
        UserRecord userRecord = JSONObject.parseObject(msg, UserRecord.class);
        String time = userRecord.getTime();
        String goodsId = userRecord.getGoodsId();
        String username = userRecord.getUsername();
        //获取秒杀商品
        SeckillGoods seckillGoods=(SeckillGoods)redisTemplate.boundHashOps(time).get(goodsId);
        //获取该商品的秒杀结束时间
        String endTime = seckillGoods.getEndTime();
        Date date = new Date(endTime);
        //判断商品是否已过活动时间
        if(System.currentTimeMillis()>= date.getTime()){
            userRecord.setMsg("商品活动时间结束,秒杀失败");
            userRecord.setStatus(3);//秒杀失败
            //更新reids中的数据
            redisTemplate.boundHashOps("secKill_userRecord").put(username,userRecord);
            //释放资源
            redisTemplate.boundHashOps("secKill_userQueueCounts").delete(username);
            return;
        }
        //判断商品的库存是否充足
        boolean flag = false;
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < userRecord.getNum(); i++) {
            Object obj = redisTemplate.boundListOps("secKillGoods_stock_queue").rightPop();
            if(obj==null){
                //库存不足
                flag=true;
                break;
            }else {
                list.add(obj);
            }
        }

        if(flag){
            //回滚库存
            redisTemplate.boundListOps("secKillGoods_stock_queue"+goodsId).leftPushAll(list);
            userRecord.setMsg("商品库存不足,请选购其他商品");
            userRecord.setStatus(3);//秒杀失败
            //更新reids中的数据
            redisTemplate.boundHashOps("secKill_userRecord").put(username,userRecord);
            //释放资源
            redisTemplate.boundHashOps("secKill_userQueueCounts").delete(username);
            return;

        }

        //构建秒杀商品订单对象
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(UUID.randomUUID().toString().replace("-",""));
        seckillOrder.setCreateTime(userRecord.getCreateTime());
        seckillOrder.setGoodsId(goodsId);
        seckillOrder.setNum(userRecord.getNum());
        seckillOrder.setUserId(username);
        seckillOrder.setStatus("0");
        BigDecimal num = new BigDecimal(userRecord.getNum() + "");
        BigDecimal costPrice = new BigDecimal(seckillGoods.getCostPrice() + "");
        String money = num.multiply(costPrice).toString();
        seckillOrder.setMoney(money);
        //保存订单数据到redis中
         redisTemplate.boundHashOps("secKillOrder").put(username,seckillOrder);

        //更新库存
        Long stockNum = redisTemplate.boundHashOps("secKillGoods_stock_num").increment(goodsId, -userRecord.getNum());
        seckillGoods.setStockCount(Integer.parseInt(stockNum+""));
        if(stockNum>0){
            redisTemplate.boundHashOps(time).put(goodsId,seckillGoods);
        }else {
            //发送同步消息，更新数据库数据 Todo
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_STORE_NONE,MqConst.ROUTING_STORE_NONE,goodsId+"");

        }
        //更新排队数据
        userRecord.setStatus(2);//等待支付
        userRecord.setOrderId(seckillOrder.getId());
        userRecord.setMoney(Double.parseDouble(money));
        userRecord.setMsg("下单成功，等待支付！");
        redisTemplate.boundHashOps("secKill_userRecord").put(username,userRecord);

        //发送延迟消息，取消超时未支付订单 TODO
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_SECKILL_ORDER_CANCEL_DEAD,MqConst.ROUTING_SECKILL_ORDER_CANCEL_DEAD,username);
    }
}
