package com.atguigu.gmall.item.service;

import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.feign.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private ListFeignClient listFeignClient;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
    /**
     * 根据skuId查询sku的详情
     *
     * @param skuId
     * @return
     */
    @Override
    public Map<String, Object> getSkuDetail(Long skuId) {
        Map<String,Object> resultMap = new HashMap<>();
        //异步执行任务一，查询skuInfo,并返回
        CompletableFuture<SkuInfo> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            resultMap.put("skuInfo", skuInfo);
            return skuInfo;
        },threadPoolExecutor);
        //与任务一并行执行任务二，查询skuImages
        CompletableFuture<Void> skuImagesFuture = CompletableFuture.runAsync(() -> {
            List<SkuImage> skuImages = productFeignClient.getskuImages(skuId);
            resultMap.put("skuImages", skuImages);
        },threadPoolExecutor);
        //与任务一并行执行任务三，查询skuPrice
        CompletableFuture<Void> skuPriceFuture = CompletableFuture.runAsync(() -> {
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            resultMap.put("skuPrice", skuPrice);
        },threadPoolExecutor);
        //任务一执行完成后执行任务四，查询SkuValueIdsMap
        CompletableFuture<Void> skuValueIdsMapCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            Map skuValueIdsMap = productFeignClient.getSkuValueIdsMap(skuInfo.getSpuId());
            resultMap.put("skuValueIdsMap", skuValueIdsMap);
        },threadPoolExecutor);
        //任务一执行完成后执行任务五，查询spu的销售属性列表
        CompletableFuture<Void> spuSaleAttrListCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            List<SpuSaleAttr> spuSaleAttrListCheckBySku = productFeignClient.getSpuSaleAttrListCheckBySku(skuId, skuInfo.getSpuId());
            resultMap.put("spuSaleAttrList", spuSaleAttrListCheckBySku);
        },threadPoolExecutor);
        //任务一执行完成后执行任务六，查询商品分类信息
        CompletableFuture<Void> baseCategoryViewCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfo -> {
            BaseCategoryView baseCategoryView = productFeignClient.getBaseCategoryView(skuInfo.getCategory3Id());
            resultMap.put("baseCategoryView", baseCategoryView);
        },threadPoolExecutor);

        //调用join() 等待任务全部执行完成,
        CompletableFuture.allOf(skuInfoCompletableFuture,
                                skuImagesFuture,
                                skuPriceFuture,
                                skuValueIdsMapCompletableFuture,
                                spuSaleAttrListCompletableFuture,
                                baseCategoryViewCompletableFuture).join();

        //查询商品后增加该商品热度值
        CompletableFuture.runAsync(()->listFeignClient.addHotScore(skuId),threadPoolExecutor);
        //返回执行结果
        return resultMap;

    }
}
