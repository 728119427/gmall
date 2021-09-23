package com.atguigu.gmall.pay.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.pay.service.PaymentService;
import com.atguigu.gmall.rabbit.config.RabbitService;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pay")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RabbitService rabbitService;

    /**
     * 获取创建微信支付的二维码链接
     * 普通订单需传orderId，totalMoney
     * 秒杀订单需传orderId，totalMoney,username(username用于从redis中获取秒杀的订单信息)
     * @param map
     * @return
     */
    @GetMapping("/create")
    public Result createPayCode(Map<String,String> map){
        Map payCodeMap = paymentService.createPayCode(map);
        return Result.ok(payCodeMap);
    }

    /**
     * 查询订单支付状态
     * @param orderId
     * @return
     */
    @GetMapping("/query")
    public Result queryOrder(Long orderId){
        Map map = paymentService.queryOrder(orderId);
        return Result.ok(map);
    }

    /**
     * 关闭订单
     * @param orderId
     * @return
     */
    @GetMapping("close")
    public Result closeOrder(Long orderId){
        Map map = paymentService.closeOrder(orderId);
        return Result.ok(map);
    }

    /**
     * 微信支付后的回调接口
     * @param request
     * @return
     */
    @RequestMapping("/order/notify")
    public String notifyAddress(HttpServletRequest request){
        //获取回调结果
        try {
            ServletInputStream is = request.getInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte [] bytes = new byte[1024];
            int len =0;
            while ((len=is.read(bytes))!=-1){
                os.write(bytes,0,len);
            }
            String xmlResult=new String(os.toByteArray());
            //将返回xml转换为map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xmlResult);
            //获取附加参数
            String attach = resultMap.get("attach");
            Map<String,String> attachMap = JSONObject.parseObject(attach, Map.class);
            //发送消息,更新支付状态,请求url的参数中指定交换机和routingKey
            rabbitService.sendMessage(attachMap.get("exchange"),
                                        attachMap.get("routingKey"), JSONObject.toJSONString(resultMap));
            //封装返回微信收到了结果
            Map<String, String> result = new HashMap<>();
            result.put("return_code","SUCCESS");
            result.put("return_msg","OK");
            //返回
            return WXPayUtil.mapToXml(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
