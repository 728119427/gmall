package com.atguigu.gmall.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall.pay.service.AliService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.NotLinkException;
import java.util.HashMap;
import java.util.Map;

/**
 * 阿里支付接口实现类
 */
@Service
public class AliServiceImpl implements AliService {
    @Value("${ali.pay.alipay_url}")
    private String payUrl; //ali请求url
    @Value("${ali.pay.app_id}")
    private String appId; //公众账号Id
    @Value("${ali.pay.app_private_key}")
    private String privateKey; //私钥
    @Value("${ali.pay.alipay_public_key}")
    private String publicKey;  //公钥
    @Value("${ali.pay.return_payment_url}")
    private String returnUrl;  //同步回调地址
    @Value("${ali.pay.notify_payment_url}")
    private String notifyUrl;   //异步回调地址


    /**
     * 创建订单
     *
     * @param orderId
     * @param money
     * @return
     */
    @Override
    public String createPay(Long orderId, Double money) {

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(
                    payUrl,
                    appId,
                    privateKey,
                    "json",
                    "UTF-8",
                    publicKey,
                    "RSA2");
            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            //封装其他请求参数
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("out_trade_no",orderId+"");
            paramMap.put("product_code","FAST_INSTANT_TRADE_PAY");
            paramMap.put("total_amount",money+"");
            paramMap.put("subject","阿里支付测试商品");
            //添加参数到请求中
            request.setBizContent(JSONObject.toJSONString(paramMap));
            //设置异步回调地址
            request.setNotifyUrl(notifyUrl);
            //设置同步回调地址
            request.setReturnUrl(returnUrl);
            AlipayTradePagePayResponse response =alipayClient.pageExecute(request);
            if(response.isSuccess()){
                //返回响应结果
                return response.getBody();
            }

        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询订单状态
     *
     * @param orderId
     * @return
     */
    @Override
    public String queryPay(Long orderId) {

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(
                    payUrl,
                    appId,
                    privateKey,
                    "json",
                    "UTF-8",
                    publicKey,
                    "RSA2");
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            //封装其他请求参数
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("out_trade_no",orderId+"");
            request.setBizContent(JSONObject.toJSONString(paramMap));
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                return response.getBody();
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }
}
