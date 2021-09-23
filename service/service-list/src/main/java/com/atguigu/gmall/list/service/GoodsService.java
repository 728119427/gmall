package com.atguigu.gmall.list.service;

import com.atguigu.gmall.model.list.Goods;

/**
 * 商品上下架管理
 */
public interface GoodsService {

    /**
     * 商品上架
     * @param skuId
     * @return
     */
    Goods upperGoods(Long skuId);

    /**
     * 商品下架
     * @param skuId
     */
    void lowerGoods(Long skuId);

    /**
     * 增加商品的热度值
     * @param skuId
     */
    Long incrHotScore(Long skuId);

}
