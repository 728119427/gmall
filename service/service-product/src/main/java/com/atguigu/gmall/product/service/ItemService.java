package com.atguigu.gmall.product.service;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.product.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 查询商品详情相关的接口
 */
public interface ItemService {
    /**
     * 根据skuId查询skuInfo
     * @param skuId
     * @return
     */
    SkuInfo getSkuInfo(Long skuId);

    /**
     * 根据catogory3Id查询baseCategoryView
     * @param category3Id
     * @return
     */
    BaseCategoryView getBaseCategoryViewByCategory3Id(Long category3Id);

    /**
     * 查询sku的价格
     * @param skuId
     * @return
     */
    BigDecimal getPrice(Long skuId);

    /**
     * 根据spuId和skuId查询商品的销售属性
     * @param skuId
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId,Long spuId);

    /**
     *查询指定spu的所有销售属性值与sku对应关系
     * @param spuId
     * @return
     */
    Map getSkuValueIdsMap(Long spuId);

    /**
     * 查询sku的图片列表
     * @param skuId
     * @return
     */
    List<SkuImage> getSkuImages(Long skuId);

    /**
     * 获取全部分类信息
     * @return
     */
    List<JSONObject> getBaseCategoryList();

    /**
     * 根据sku的品牌id查询品牌
     * @param tmId
     * @return
     */
    BaseTrademark getTrademarkById(Long tmId);

    /**
     * 根据skuId查询商品对应的平台属性
     * @param skuId
     * @return
     */
    List<BaseAttrInfo> getBaseAttrInfoListBySkuId(Long skuId);

    /**
     * 扣库存
     * @param skuStore
     */
    void decountStore(Map<String,String> skuStore);

    /**
     * 回滚库存
     * @param skuStore
     */
    void rollbackStore(Map<String,String> skuStore);
}
