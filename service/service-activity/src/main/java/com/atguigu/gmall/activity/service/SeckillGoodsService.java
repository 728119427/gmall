package com.atguigu.gmall.activity.service;

import com.atguigu.gmall.model.activity.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {

    /**
     * 查询指定时间段内的秒杀商品列表
     * @param time
     * @return
     */
    List<SeckillGoods> getSeckillGoodsList(String time);

    /**
     * 查询商品的详情的信息
     * @param id
     * @param time
     * @return
     */
     SeckillGoods getSeckillGoods(String id, String time);
}
