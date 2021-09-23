package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(value = "service-item",path = "/api/item")
public interface ItemFeignClient {

    /**
     * 查询商品详情
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuDetail/{skuId}")
    public Result<Map> getSkuDetail(@PathVariable("skuId") Long skuId);
}
