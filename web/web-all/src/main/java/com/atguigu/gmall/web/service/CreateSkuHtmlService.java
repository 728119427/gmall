package com.atguigu.gmall.web.service;

/**
 * 生成商品详情静态页面的接口类
 */
public interface CreateSkuHtmlService {

    /**
     * 生成商品详情静态页面
     * @param skuId
     */
    void createSkuHtml(Long skuId);
}
