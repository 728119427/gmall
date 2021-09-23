package com.atguigu.gmall.pay.service;

import java.util.Map;

public interface PaymentService {

    /**
     * 获取创建微信支付的二维码链接
     * @param map
     * @return
     */
    Map createPayCode(Map<String,String> map);

    /**
     * 查询订单的支付状态
     * @param orderId
     * @return
     */
    public Map queryOrder(Long orderId);

    /**
     * 关闭交易
     * @param orderId
     * @return
     */
    public Map closeOrder(Long orderId);
}
