package com.atguigu.gmall.activity.mapper;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {
    /**
     * 更新秒杀商品库存
     * @param num
     * @param goodsId
     */
    @Update("UPDATE seckill_goods SET stock_count=#{store} WHERE id=#{goodsId}")
    void updateStore(@Param("store") Integer num,@Param("goodsId") String goodsId);
}
