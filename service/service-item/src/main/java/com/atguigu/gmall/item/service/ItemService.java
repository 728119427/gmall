package com.atguigu.gmall.item.service;

import java.util.Map;

public interface ItemService {
    /**
     * 根据skuId查询sku的详情
     * @param skuId
     * @return
     */
    Map<String,Object> getSkuDetail(Long skuId);
}
