package com.atguigu.gmall.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.util.HttpClient;
import com.atguigu.gmall.pay.constant.WXConst;
import com.atguigu.gmall.pay.service.PaymentService;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Value("${weixin.pay.appid}")
    private String appid;  //公众账号Id

    @Value("${weixin.pay.partner}")
    private String partner;  //商户号

    @Value("${weixin.pay.partnerkey}")
    private String partnerkey;  //商户key

    @Value("${weixin.pay.notifyUrl}")
    private String notifyUrl;  //回调地址

    /**
     * 获取创建微信支付的二维码链接
     *
     * @param map
     * @return
     */
    @Override
    public Map createPayCode(Map<String,String> map) {
        //请求地址
        String paymentUrl = WXConst.WX_PAYMENT_URL;
        //封装参数
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",appid);
        paramMap.put("mch_id",partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("body","测试微信支付的商品");
        paramMap.put("out_trade_no",map.get("orderId"));
        paramMap.put("total_fee",map.get("totalMoney"));
        paramMap.put("spbill_create_ip","123.12.12.123");
        paramMap.put("notify_url",notifyUrl);
        paramMap.put("trade_type","NATIVE");
        //添加附加参数，异步回传获取
        paramMap.put("attach", JSONObject.toJSONString(paramMap));
        //获取返回结果
        return getResultMap(paramMap,paymentUrl);


    }

    /**
     * 获取返回结果
     * @param paramMap
     * @param paymentUrl
     * @return
     */
    private Map<String, String> getResultMap(Map<String, String> paramMap,String paymentUrl) {
        try {
            String xmlParam = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            log.info("xmlParam: "+xmlParam);
            HttpClient httpClient = new HttpClient(paymentUrl);
            httpClient.setXmlParam(xmlParam);
            httpClient.setHttps(true);
            httpClient.post();
            //获取返回结果
            String content = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            if(resultMap.get("return_code").equals("SUCCESS") && resultMap.get("result_code").equals("SUCCESS")){
                return resultMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 查询订单的支付状态
     *
     * @param orderId
     * @return
     */
    @Override
    public Map queryOrder(Long orderId) {
        //请求地址
        String queryUrl = WXConst.WX_PAYMENT_QUERY_URL;
        //包装参数
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",appid);
        paramMap.put("mch_id",partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("out_trade_no",orderId+"");
        //获取返回结果
        return getResultMap(paramMap,queryUrl);

    }

    /**
     * 关闭交易
     *
     * @param orderId
     * @return
     */
    @Override
    public Map closeOrder(Long orderId) {
        //请求地址
        String queryUrl = WXConst.WX_PAYMENT_CLOSE_URL;
        //包装参数
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appid",appid);
        paramMap.put("mch_id",partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("out_trade_no",orderId+"");
        //获取返回结果
        return getResultMap(paramMap,queryUrl);
    }
}
