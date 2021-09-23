package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;
import java.util.Map;

/**
 * 购物车管理接口
 */
public interface CartInfoService {
    /**
     * 新增购物车
     * @param skuId
     * @param num
     * @param userId
     * @return
     */
    public CartInfo addCart(Long skuId, Integer num, String userId);


    /**
     * 修改购物车数量
     * @param skuId
     * @param num
     * @param userId
     * @return
     */
    public CartInfo updateCart(Long skuId, Integer num, String userId);

    /**
     * 删除购物车数据
     * @param skuId
     * @param userId
     */
    public void delCart(Long skuId, String userId);

    /**
     * 修改商品的选中状态
     * @param skuId
     * @param userId
     * @param isCheck
     * @return
     */
    public CartInfo updateCartCheckStatus(Long skuId, String userId, Boolean isCheck);


    /**
     * 查询购物车信息--从数据库查
     * @param userId
     * @return
     */
    public List<CartInfo> getCartInfo(String userId);


    /**
     * 合并购物车
     * @param cartInfos
     * @param userId
     */
    public void mergeCartInfo(List<CartInfo> cartInfos, String userId);

    /**
     * 获取用户选中的购物车列表以及相应的数量和价格
     * @param username
     * @return
     */
    Map<String,Object> getOrderComfirmInfo(String username);

    /**
     * 查询用户选中的购物车列表
     * @param username
     * @return
     */
    List<CartInfo> getCheckedCartInfos(String username);

    /**
     * 添加订单后删除选中的购物城
     * @param username
     */
    void deleteCartListAfterOrderAdd(String username);
}
