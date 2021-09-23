package com.atguigu.gmall.pay.controller;

import com.atguigu.gmall.pay.service.AliService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/ali")
public class AliPayController {
    @Autowired
    private AliService aliService;

    /**
     * 生成支付页面
     * @param orderId
     * @param totalMoney
     * @return
     */
    @GetMapping("/pay")
    public String createPay(Long orderId,Double totalMoney){
        return aliService.createPay(orderId,totalMoney);
    }

    /**
     * 查询订单
     * @param orderId
     * @return
     */
    @GetMapping("/query")
    public String queryPay(Long orderId){
        return aliService.queryPay(orderId);
    }

    /**
     * 同步回调接口
     * @param data
     * @return
     */
    @GetMapping("/return/callback")
    public String returnCallback(@RequestParam Map<String,Object> data){
        System.out.println(data);
        return "同步回调成功";
    }

    /**
     * 异步回调接口
     * @param data
     * @return
     */
    @PostMapping("/notify/callback")
    public String notifyCallback(@RequestParam Map<String,Object> data){
        System.out.println(data);
        return "success";
    }
}
