package com.atguigu.gmall.activity.service;

import com.atguigu.gmall.activity.pojo.UserRecord;

import java.util.Map;

public interface SeckillOrderService {

    /**
     * 排队下单
     * @param time
     * @param id
     * @param num
     * @return
     */
    UserRecord addOrder(String time, String id,Integer num);

    /**
     * 根据用户名查询排队对象
     * @param username
     * @return
     */
    UserRecord getUserRecord(String username);

    /**
     * 删除超时未支付订单
     * @param username
     */
    void cancelSecKillOrder(String username);

    /**
     * 更新秒杀订单状态
     * @param notifyMap
     */
    void updateOrderStatus(Map<String,String> notifyMap);
}
