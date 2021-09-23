package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

import java.util.Map;

public interface OrderService {
    /**
     * 添加订单
     * @param orderInfo
     * @return
     */
    Map<String,Object> addOrder(OrderInfo orderInfo);

    /**
     * 取消订单
     * @param orderInfoId
     */
    void cancelOrder(Long orderInfoId);


    /**
     * 更改订单状态
     * @param notifyMap
     */
    void updateOrderStatus(Map<String,String> notifyMap);
}
