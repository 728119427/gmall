package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.RedisTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/redis/product/test")
public class RedisTestController {
    @Autowired
    private RedisTestService redisTestService;

    @GetMapping("/demo1")
    public Result demo1(){
        redisTestService.lockDemo();
        return Result.ok();
    }
}
