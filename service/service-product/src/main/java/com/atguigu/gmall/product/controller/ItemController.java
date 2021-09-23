package com.atguigu.gmall.product.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.cache.GmallCache;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ItemService;
import com.atguigu.gmall.product.service.impl.ItemServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product/item")
public class ItemController {
    @Autowired
    private ItemService itemService;

    /**
     * 查询sku的详细信息
     * @param skuId
     * @return
     */
    @GmallCache(prefix = "skuInfo:")
    @GetMapping("/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId){
        return itemService.getSkuInfo(skuId);
    }

    /**
     * 查询sku的图片列表
     * @param skuId
     * @return
     */
    @GmallCache(prefix = "skuImages:")
    @GetMapping("/getSkuImages/{skuId}")
    public List<SkuImage> getskuImages(@PathVariable("skuId") Long skuId){
        return itemService.getSkuImages(skuId);
    }

    /**
     * 根据category3Id查询分类信息
     * @param category3Id
     * @return
     */
    @GmallCache(prefix = "skuCategoryView:")
    @GetMapping("/getCategoryView/{category3Id}")
    public BaseCategoryView getBaseCategoryView(@PathVariable("category3Id") Long category3Id){
        return itemService.getBaseCategoryViewByCategory3Id(category3Id);
    }

    /**
     * 查询sku的价格信息
     * @param skuId
     * @return
     */
    @GmallCache(prefix = "skuPrice:")
    @GetMapping("/getSkuPrice/{skuId}")
    public BigDecimal getSkuPrice(@PathVariable("skuId") Long skuId){
        return itemService.getPrice(skuId);
    }

    /**
     * 根据spuId和skuId查询商品的销售属性
     * @return
     */
    @GmallCache(prefix = "spuSaleAttrList:")
    @GetMapping("/getSpuSaleAttrListCheckBySku/{skuId}/{spuId}")
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(@PathVariable("skuId") Long skuId,@PathVariable("spuId") Long spuId){
        return itemService.getSpuSaleAttrListCheckBySku(skuId,spuId);
    }

    /**
     * 查询指定spu的所有销售属性值与sku对应关系
     * @param spuId
     * @return
     */
    @GmallCache(prefix = "skuValueIdsMap:")
    @GetMapping("/getSkuValueIdsMap/{spuId}")
    public Map getSkuValueIdsMap(@PathVariable("spuId") Long spuId){
        return itemService.getSkuValueIdsMap(spuId);
    }

    /**
     * 获取商品的分类信息
     * @return
     */
    @GmallCache(prefix = "baseCategoryList:")
    @GetMapping("/getBaseCategoryList")
    public List<JSONObject>  getBaseCategoryList(){
        List<JSONObject> list = itemService.getBaseCategoryList();
        return list;
    }

    /**
     * 根据sku的品牌id查询品牌信息
     * @param tmId
     * @return
     */
    @GetMapping("/getTrademark/{tmId}")
    public BaseTrademark getBaseTrademarkById(@PathVariable("tmId") Long tmId){
       return itemService.getTrademarkById(tmId);
    }

    /**
     * 根据skuId查询该商品的平台属性
     * @param skuId
     * @return
     */
    @GetMapping("/getBaseAttrInfoList/{skuId}")
    public List<BaseAttrInfo> getBaseAttrInfoList(@PathVariable("skuId") Long skuId){
        return itemService.getBaseAttrInfoListBySkuId(skuId);
    }

    /**
     * 删除库存
     * @param skuStore
     * @return
     */
    @DeleteMapping("decountStore")
    public Result decountStore(@RequestParam Map<String,String> skuStore){
        itemService.decountStore(skuStore);
        return Result.ok();
    }

    /**
     * 回滚库存
     * @param skuStore
     * @return
     */
    @PutMapping("rollbackStore")
    public Result rollbackStore(@RequestParam Map<String,String> skuStore){
        itemService.rollbackStore(skuStore);
        return Result.ok();
    }
}
