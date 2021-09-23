package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
    @RequestMapping("/api/item")
public class ItemController {
    @Autowired
    private ItemService itemService;

    /**
     * 查询商品详情
     * @param skuId
     * @return
     */
    @GetMapping("/getSkuDetail/{skuId}")
    public Result<Map> getSkuDetail(@PathVariable("skuId") Long skuId){
        Map<String, Object> skuDetail = itemService.getSkuDetail(skuId);
        return Result.ok(skuDetail);
    }
}
