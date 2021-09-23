package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.GoodsService;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/list")
public class ListApiController {
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private SearchService searchService;

    @GetMapping("/createIndex")
    public Result createIndex(){
        elasticsearchRestTemplate.createIndex(Goods.class);
        elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    /**
     * 商品上架
     * @param skuId
     * @return
     */
    @GetMapping("/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable("skuId") Long skuId){
        Goods goods = goodsService.upperGoods(skuId);
        return Result.ok(goods);
    }

    /**
     * 商品下架
     * @param skuId
     * @return
     */
    @GetMapping("/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable("skuId") Long skuId){
        goodsService.lowerGoods(skuId);
        return Result.ok();
    }

    /**
     * 更新热度值
     * @param skuId
     * @return
     */
    @PutMapping("/addHotScore/{skuId}")
    public Result addHotScore(@PathVariable("skuId") Long skuId){
        Long score = goodsService.incrHotScore(skuId);
        return Result.ok(score);
    }

    /**
     * 根据条件查询商品
     * @param searchMap
     * @return
     */
    @GetMapping("/search")
    public Map<String,Object> search(@RequestParam Map<String,String> searchMap){
        return searchService.search(searchMap) ;
    }

}
