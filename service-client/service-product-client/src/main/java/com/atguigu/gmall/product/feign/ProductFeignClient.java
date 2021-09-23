package com.atguigu.gmall.product.feign;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@FeignClient(value = "service-product",path = "/api/product/item")
public interface ProductFeignClient {

    /**
     * 查询sku的详细信息
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);

    /**
     * 查询sku的图片列表
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuImages/{skuId}")
    public List<SkuImage> getskuImages(@PathVariable("skuId") Long skuId);


    /**
     * 根据category3Id查询分类信息
     * @param category3Id
     * @return
     */
    @GetMapping("/getCategoryView/{category3Id}")
    public BaseCategoryView getBaseCategoryView(@PathVariable("category3Id") Long category3Id);

    /**
     * 查询sku的价格信息
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable("skuId") Long skuId);

    /**
     * 根据spuId和skuId查询商品的销售属性
     * @return
     */
    @GetMapping("/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("skuId") Long skuId, @PathVariable("spuId") Long spuId);
    /**
     * 查询指定spu的所有销售属性值与sku对应关系
     * @param spuId
     * @return
     */
    @GetMapping("/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable("spuId") Long spuId);

    /**
     * 获取商品的分类信息
     * @return
     */
    @GetMapping("/getBaseCategoryList")
    public List<JSONObject>  getBaseCategoryList();

    /**
     * 根据sku的品牌id查询品牌信息
     * @param tmId
     * @return
     */
    @GetMapping("/getTrademark/{tmId}")
    public BaseTrademark getBaseTrademarkById(@PathVariable("tmId") Long tmId);

    /**
     * 根据skuId查询该商品的平台属性
     * @param skuId
     * @return
     */
    @GetMapping("/getBaseAttrInfoList/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfoList(@PathVariable("skuId") Long skuId);

    /**
     * 扣库存
     * @param skuStore
     * @return
     */
    @DeleteMapping("decountStore")
    public Result decountStore(@RequestParam Map<String,String> skuStore);

    /**
     * 回滚库存
     * @param skuStore
     * @return
     */
    @PutMapping("rollbackStore")
    public Result rollbackStore(@RequestParam Map<String,String> skuStore);
}
