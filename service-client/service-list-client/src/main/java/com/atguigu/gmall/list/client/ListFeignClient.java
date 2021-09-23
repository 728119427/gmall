package com.atguigu.gmall.list.client;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "service-list",path = "/api/list")
public interface ListFeignClient {

    /**
     * 商品上架
     * @param skuId
     * @return
     */
    @GetMapping("/upperGoods/{skuId}")
    public Result upperGoods(@PathVariable("skuId") Long skuId);

    /**
     * 商品下架
     * @param skuId
     * @return
     */
    @GetMapping("/lowerGoods/{skuId}")
    public Result lowerGoods(@PathVariable("skuId") Long skuId);

    /**
     * 更新热度值
     * @param skuId
     * @return
     */
    @PutMapping("/addHotScore/{skuId}")
    public Result addHotScore(@PathVariable("skuId") Long skuId);

    /**
     * 根据条件查询商品
     * @param searchMap
     * @return
     */
    @GetMapping("/search")
    public Map<String,Object> search(@RequestParam Map<String,String> searchMap);

}
