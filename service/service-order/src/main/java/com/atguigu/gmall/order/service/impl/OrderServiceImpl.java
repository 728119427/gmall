package com.atguigu.gmall.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.cart.feign.CartFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.product.feign.ProductFeignClient;
import com.atguigu.gmall.rabbit.config.RabbitService;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class OrderServiceImpl implements OrderService {
    @Autowired
    private CartFeignClient cartFeignClient;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 添加订单
     *
     * @param orderInfo
     * @return
     */
    @Override
    public Map<String, Object> addOrder(OrderInfo orderInfo) {
        //初始化返回结果
        Map<String,Object> resultMap = new HashMap<>();
        //校验参数
        if(orderInfo==null){
            return resultMap;
        }
        //设置orderInfo的参数
        orderInfo.setOrderStatus(OrderStatus.UNPAID.getComment());
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.getComment());
        orderInfo.setCreateTime(new Date());
        orderInfo.setExpireTime(new Date(System.currentTimeMillis()+20*60*1000));
        //获取选中的购物车列表，
        List<CartInfo> checkedCartInfos = cartFeignClient.getCheckedCartInfos();
        if(ObjectUtils.isEmpty(checkedCartInfos)){
            return resultMap;
        }
        //包装为orderDetail
        List<OrderDetail> orderDetails = checkedCartInfos.stream().map(cartInfo -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setImgUrl(cartInfo.getImgUrl());
            //获取实时商品价格
            orderDetail.setOrderPrice(productFeignClient.getSkuPrice(cartInfo.getSkuId()));
            orderDetail.setSkuId(cartInfo.getSkuId());
            orderDetail.setSkuName(cartInfo.getSkuName());
            orderDetail.setSkuNum(cartInfo.getSkuNum());
            return orderDetail;
        }).collect(Collectors.toList());
        //设置到orderInfo
        orderInfo.setOrderDetailList(orderDetails);
        //计算总金额
        orderInfo.sumTotalAmount();
        //添加订单
        int insert = orderInfoMapper.insert(orderInfo);
        Map<String,String> skuStore = new HashMap<>();
        if(insert>0){
            //订单添加成功
            //补全orderDetail的参数，并添加到数据库，同时获取扣减库存的数据
            for (OrderDetail orderDetail : orderDetails) {
                orderDetail.setOrderId(orderInfo.getId());
                Long skuId = orderDetail.getSkuId();
                Integer skuNum = orderDetail.getSkuNum();
                //保存扣减库存的数据
                skuStore.put(skuId+"",skuNum+"");
                //插入数据库
                orderDetailMapper.insert(orderDetail);
            }
        }


        //删除购物车选中数据
        cartFeignClient.deleteCartList();
        //扣减库存
        productFeignClient.decountStore(skuStore);
        //发送延迟消息，取消超时未支付订单
        rabbitTemplate.convertAndSend(MqConst.EXCHANGE_DIRECT_ORDER_CANCEL_DEAD,
                MqConst.ROUTING_ORDER_CANCEL_DEAD, (orderInfo.getId()+120000) + "",message -> {
                    message.getMessageProperties().setExpiration("360000");
                    return message;
                });
        //返回结果
        resultMap.put("orderId",orderInfo.getId());
        resultMap.put("totalAmount",orderInfo.getTotalAmount());
        return resultMap;
    }

    @Autowired
    private RabbitService rabbitService;
    /**
     * 取消订单
     *
     * @param orderInfoId
     */
    @Override
    public void cancelOrder(Long orderInfoId) {
        //校验参数
        if(orderInfoId==null){
            throw new RuntimeException("orderInfoId为空");
        };
        //查询orderInfo
        OrderInfo orderInfo = orderInfoMapper.selectById(orderInfoId);
        if(orderInfo==null || orderInfo.getId()==null){
            throw new RuntimeException("该订单不存在！");
        }
        //如果订单状态为未支付则修改为取消订单
        if (!OrderStatus.UNPAID.getComment().equals(orderInfo.getOrderStatus())){
            //订单状态为其他
            return;
        }
        //关闭交易 TODO
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_PAYMENT_CLOSE,MqConst.ROUTING_PAYMENT_CLOSE,orderInfoId+"");
        //订单未支付，取消订单
        orderInfo.setOrderStatus(OrderStatus.CLOSED.getComment());
        orderInfo.setProcessStatus(ProcessStatus.CLOSED.getComment());
        //修改订单状态
        orderInfoMapper.updateById(orderInfo);
        //获取回滚库存的数据
        Map<String,String> skuStore = new HashMap<>();
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(new LambdaQueryWrapper<OrderDetail>()
                .eq(OrderDetail::getOrderId, orderInfo.getId()));
        for (OrderDetail orderDetail : orderDetails) {
            Long skuId = orderDetail.getSkuId();
            Integer skuNum = orderDetail.getSkuNum();
            skuStore.put(skuId+"",skuNum+"");
        }
        //回滚库存
        productFeignClient.rollbackStore(skuStore);


    }

    /**
     * 更改订单状态
     *
     * @param notifyMap
     */
    @Override
    public void updateOrderStatus(Map<String,String> notifyMap) {
        //获取订单信息
        String orderId = notifyMap.get("out_trade_no");
        OrderInfo orderInfo = orderInfoMapper.selectById(Long.parseLong(orderId));
        //判断，解决幂等性
        if(!OrderStatus.UNPAID.getComment().equals(orderInfo.getOrderStatus())){
            //订单状态不是未支付
            return;
        }
        //订单状态为未支付，则改为已支付
        orderInfo.setOrderStatus(OrderStatus.PAID.getComment());
        orderInfo.setProcessStatus(ProcessStatus.PAID.getComment());
        orderInfo.setOutTradeNo((String)notifyMap.get("transaction_id"));
        orderInfo.setTradeBody(JSONObject.toJSONString(notifyMap));
        orderInfoMapper.updateById(orderInfo);
    }
}
