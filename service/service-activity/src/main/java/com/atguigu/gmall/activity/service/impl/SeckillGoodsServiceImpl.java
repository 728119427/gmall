package com.atguigu.gmall.activity.service.impl;

import com.atguigu.gmall.activity.service.SeckillGoodsService;
import com.atguigu.gmall.model.activity.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 查询指定时间段内的秒杀商品列表
     *
     * @param time
     * @return
     */
    @Override
    public List<SeckillGoods> getSeckillGoodsList(String time) {
        return redisTemplate.boundHashOps(time).values();
    }

    /**
     * 查询商品的详情的信息
     *
     * @param id
     * @param time
     * @return
     */
    @Override
    public SeckillGoods getSeckillGoods(String id, String time) {
        return (SeckillGoods) redisTemplate.boundHashOps(time).get(id);
    }
}
