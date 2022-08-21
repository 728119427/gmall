package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.util.GmallThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/addOrder")
    public Result<Map> addOrder(@RequestBody OrderInfo orderInfo){
        //获取用户名，补全参数
        String userName = GmallThreadLocalUtils.getUserName();
        orderInfo.setUserId(userName);
        Map<String, Object> map = orderService.addOrder(orderInfo);
        return Result.ok(map);
    }

}
