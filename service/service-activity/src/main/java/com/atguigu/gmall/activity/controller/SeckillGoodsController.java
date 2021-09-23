package com.atguigu.gmall.activity.controller;

import com.atguigu.gmall.activity.service.SeckillGoodsService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.activity.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seckill/goods")
public class SeckillGoodsController {
    @Autowired
    private SeckillGoodsService seckillGoodsService;

    /**
     * 查询指定时间段内的商品
     * @param time
     * @return
     */
    @GetMapping("/getSeckillGoodsList")
    public Result<List<SeckillGoods>> getSeckillGoodsList(String time){
        List<SeckillGoods> seckillGoodsList = seckillGoodsService.getSeckillGoodsList(time);
        return Result.ok(seckillGoodsList);
    }

    /**
     * 查询商品详情
     * @param time
     * @return
     */
    @GetMapping("/getSeckillGoods")
    public Result<SeckillGoods> getSeckillGood(String time,String id){
        SeckillGoods seckillGoods = seckillGoodsService.getSeckillGoods(id, time);
        return Result.ok(seckillGoods);
    }

}
