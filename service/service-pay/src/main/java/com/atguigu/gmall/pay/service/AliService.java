package com.atguigu.gmall.pay.service;

/**
 * 阿里支付接口
 */
public interface AliService {

    /**
     * 创建订单
     * @param orderId
     * @param money
     * @return
     */
    String createPay(Long orderId,Double money);

    /**
     * 查询订单状态
     * @param orderId
     * @return
     */
    String queryPay(Long orderId);

}
