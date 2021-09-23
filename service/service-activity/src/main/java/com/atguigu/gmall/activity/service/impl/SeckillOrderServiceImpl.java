package com.atguigu.gmall.activity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.activity.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.activity.mapper.SeckillOrderMapper;
import com.atguigu.gmall.activity.pojo.SeckillOrder;
import com.atguigu.gmall.activity.pojo.UserRecord;
import com.atguigu.gmall.activity.service.SeckillOrderService;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.rabbit.config.RabbitService;
import com.atguigu.gmall.rabbit.constant.MqConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitService rabbitService;
    /**
     * 排队下单
     *
     * @param time
     * @param id
     * @param num
     * @return
     */
    @Override
    public UserRecord addOrder(String time, String id, Integer num) {
        String username="zhangsan";
        //校验参数
        if(StringUtils.isEmpty(time)|| StringUtils.isEmpty(id)|| num==null){
            return null;
        }
        //构建秒杀商品排队对象
        UserRecord userRecord = new UserRecord();
        userRecord.setCreateTime(new Date());
        userRecord.setGoodsId(id);
        userRecord.setMsg("排队中");
        userRecord.setNum(num);
        userRecord.setStatus(1);//排队中
        userRecord.setTime(time);
        userRecord.setUsername(username);
        //记录用户的排队次数
        Long counts = redisTemplate.boundHashOps("secKill_userQueueCounts").increment(username, 1);
        if(counts>1){
            userRecord.setStatus(3);//秒杀失败
            userRecord.setMsg("重复下单！");
        }
        //将排队对象保存到reids中以便查询
        redisTemplate.boundHashOps("secKill_userRecord").put(username, userRecord);
        //发送消息，异步完成下单
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_SECKILL_USER,MqConst.ROUTING_SECKILL_USER, JSONObject.toJSONString(userRecord));
        return userRecord;
    }


    /**
     * 根据用户名查询排队对象
     *
     * @param username
     * @return
     */
    @Override
    public UserRecord getUserRecord(String username) {
        return (UserRecord) redisTemplate.boundHashOps("secKill_userRecord").get(username);
    }

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    /**
     * 删除超时未支付订单
     *
     * @param username
     */
    @Override
    public void cancelSecKillOrder(String username) {
        //暂时将username写为zhangsan方便测试
        username="zhangsan";
        //查询订单信息
        SeckillOrder seckillOrder=(SeckillOrder)redisTemplate.boundHashOps("seckillOrder").get(username);
        //校验参数
        if(seckillOrder==null || !seckillOrder.getStatus().equals("0")){
            //订单不存在或者订单状态不是未支付
            return;
        }
        //修改订单状态,取消
        seckillOrder.setStatus("3");
        //保存到数据库
        seckillOrderMapper.insert(seckillOrder);
        //删除再redis中的订单数据
        redisTemplate.boundHashOps("seckillOrder").delete(username);
        //删除排队标识，释放资源
        redisTemplate.boundHashOps("secKill_userQueueCounts").delete(username);
        //修改排队对象信息
        UserRecord userRecord=(UserRecord)redisTemplate.boundHashOps("secKill_userRecord").get(username);
        userRecord.setStatus(5);
        userRecord.setMsg("订单超时未支付，已取消！");
        redisTemplate.boundHashOps("secKill_userRecord").put(username,userRecord);
        //获取redis中的商品信息
        SeckillGoods seckillGoods=(SeckillGoods)redisTemplate.boundHashOps(userRecord.getTime()).get(userRecord.getGoodsId());
        //判断redis中的商品库存是否大于0
        if(seckillGoods.getStockCount()>0){
            //回滚队列
            Integer num = userRecord.getNum();
            String goodsId = userRecord.getGoodsId();
            for (int i = 0; i <num ; i++) {
                redisTemplate.boundListOps("secKillGoods_stock_queue"+ goodsId).leftPush(goodsId);
            }
            //回滚库存值
            Long stockNum = redisTemplate.boundHashOps("secKillGoods_stock_num").increment(goodsId + "", userRecord.getNum());
            //重新设置商品的库存值，更新到redis
            seckillGoods.setStockCount(Integer.parseInt(stockNum+""));
            redisTemplate.boundHashOps(userRecord.getTime()).put(goodsId,seckillGoods);

        }else {
            //删除redis中的商品，此时商品库存为0
            redisTemplate.boundHashOps(userRecord.getTime()).delete(userRecord.getGoodsId());
            //更新数据库中的商品库存，通过定时任务将商品添加到redis
            seckillGoodsMapper.updateStore(userRecord.getNum(),userRecord.getGoodsId());
        }
    }


    /**
     * 更新秒杀订单状态
     *
     * @param notifyMap
     */
    @Override
    public void updateOrderStatus(Map<String,String> notifyMap) {
        //获取username
        String attach = notifyMap.get("attach");
        Map<String,String> map = JSONObject.parseObject(attach, Map.class);
        String username = map.get("username");
        //获取订单信息
        SeckillOrder seckillOrder=(SeckillOrder)redisTemplate.boundHashOps("secKillOrder").get(username);
        //校验
        if(seckillOrder==null || !seckillOrder.getStatus().equals("O")){
            //状态不是未支付
            return;
        }
        //修改订单状态
        seckillOrder.setStatus("1");
        seckillOrder.setOutTradeNo(notifyMap.get("trade_no"));//微信支付流水单号
        //更新redis订单数据
        redisTemplate.boundHashOps("secKillOrder").put(username,seckillOrder);
        //保存到数据库
        seckillOrderMapper.insert(seckillOrder);
        //删除排队标识，释放资源
        redisTemplate.boundHashOps("secKill_userQueueCounts").delete(username);
        //更新排队对象信息
        UserRecord userRecord=(UserRecord)redisTemplate.boundHashOps("secKill_userRecord").get(username);
        userRecord.setStatus(4);
        userRecord.setMsg("订单已支付！");
        redisTemplate.boundHashOps("secKill_userRecord").put(username,userRecord);
    }
}
