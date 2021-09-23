package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ProductFeignClient;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(rollbackFor = Exception.class)
public class CartInfoServiceImpl implements CartInfoService {
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CartInfoMapper cartInfoMapper;

    /**
     * 新增购物车
     *
     * @param skuId
     * @param num
     * @param userId
     * @return
     */
    @Override
    public CartInfo addCart(Long skuId, Integer num, String userId) {
        //参数校验
        if(skuId==null || num==null || num<=0 || StringUtils.isEmpty(userId)){
            throw new RuntimeException("参数错误");
        }
        //查询商品详情
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if(skuInfo==null){
            throw new RuntimeException("商品不存在！");
        }
        //查询该商品购物车详情
        CartInfo cartInfoDb = cartInfoMapper.selectOne(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getSkuId, skuId)
                .eq(CartInfo::getUserId, userId));
        if(cartInfoDb!=null){
            //更新购物车
            return updateCart(skuId, num,userId);
        }else {
            //将商品封装为购物车
            CartInfo cartInfo = new CartInfo();
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo.setSkuId(skuInfo.getId());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setSkuNum(num);
            cartInfo.setUserId(userId);
            //添加到数据库
            cartInfoMapper.insert(cartInfo);
            //添加到redis
            redisTemplate.boundHashOps("cart:"+userId+":info").put(skuId+"",cartInfo);
            return cartInfo;
        }


    }

    /**
     * 修改购物车数量
     *
     * @param skuId
     * @param num
     * @param userId
     * @return
     */
    @Override
    public CartInfo updateCart(Long skuId, Integer num, String userId) {
        //参数校验
        if(skuId==null || num==null || StringUtils.isEmpty(userId)){
            throw new RuntimeException("参数错误");
        }
//        if(num==0){
//            //删除购物车信息
//            delCart(skuId,userId);
//            return new CartInfo();
//        }
        //查询该商品购物车详情
        CartInfo cartInfo = cartInfoMapper.selectOne(new LambdaQueryWrapper<CartInfo>()
                                                            .eq(CartInfo::getSkuId, skuId)
                                                            .eq(CartInfo::getUserId, userId));
        if(cartInfo==null){
            //购物车中原来不存在该商品，则直接添加
            return addCart(skuId,num,userId);
        }
        //修改后商品数量小于等于0,直接删除
        if(cartInfo.getSkuNum()+num<=0){
            delCart(skuId,userId);
            return new CartInfo();
        }
        //正常修改
        cartInfo.setSkuNum(cartInfo.getSkuNum()+num);
        cartInfoMapper.updateById(cartInfo);
        //更新reids
        redisTemplate.boundHashOps("cart:"+userId+":info").put(skuId+"",cartInfo);
        return cartInfo;
    }

    /**
     * 删除购物车数据
     *
     * @param skuId
     * @param userId
     */
    @Override
    public void delCart(Long skuId, String userId) {
        //校验参数
        if(skuId==null || StringUtils.isEmpty(userId)){
            throw new RuntimeException("参数错误!");
        }
        //删除数据
        cartInfoMapper.delete(new LambdaQueryWrapper<CartInfo>()
                                    .eq(CartInfo::getSkuId,skuId)
                                    .eq(CartInfo::getUserId,userId));
        //更新到redis
        redisTemplate.boundHashOps("cart:"+userId+":info").delete(skuId+"");
    }

    /**
     * 修改商品的选中状态
     *
     * @param skuId
     * @param userId
     * @param isCheck
     * @return
     */
    @Override
    public CartInfo updateCartCheckStatus(Long skuId, String userId, Boolean isCheck) {
        //参数校验
        if(skuId==null || StringUtils.isEmpty(userId)){
            throw new RuntimeException("参数错误");
        }
        //查询该商品购物车详情
        CartInfo cartInfo = cartInfoMapper.selectOne(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getSkuId, skuId)
                .eq(CartInfo::getUserId, userId));
        //购物车数据不存在，无法修改
        if(cartInfo==null){
            return new CartInfo();
        }
        //修改状态，应该定义枚举或常量
        cartInfo.setIsChecked(isCheck?1:0);
        //更新到数据库
        cartInfoMapper.updateById(cartInfo);
        //更新到redis
        redisTemplate.boundHashOps("cart:"+userId+":info").put(skuId+"",cartInfo);
        //返回更新结果
        return cartInfo;
    }

    /**
     * 查询购物车信息--从数据库查
     *
     * @param userId
     * @return
     */
    @Override
    public List<CartInfo> getCartInfo(String userId) {
        //参数校验
        if(StringUtils.isEmpty(userId)){
            throw new RuntimeException("参数不能为空！");
        }
        //从redis中查询
        List values = redisTemplate.boundHashOps("cart:" + userId + ":info").values();
        if(!ObjectUtils.isEmpty(values)){
            //redis中有数据则直接返回
            return values;
        }
        //redis中不存在则从数据库中查询
        List<CartInfo> cartInfos = cartInfoMapper.selectList(new LambdaQueryWrapper<CartInfo>().eq(CartInfo::getUserId, userId));
        if(ObjectUtils.isEmpty(cartInfos)){
            //数据库中页没有，缓存空值到redis
            redisTemplate.boundHashOps("cart:"+userId+":info").put("null","null");
            //设置过期时间
            redisTemplate.boundHashOps("cart:"+userId+":info").expire(300L, TimeUnit.SECONDS);
        }else {
            //将数据缓存redis
            for (CartInfo cartInfo : cartInfos) {
                redisTemplate.boundHashOps("cart:"+userId+":info").put(cartInfo.getSkuId()+"",cartInfo);
            }
        }

        return cartInfos;
    }

    /**
     * 合并购物车
     *
     * @param cartInfos
     * @param userId
     */
    @Override
    public void mergeCartInfo(List<CartInfo> cartInfos, String userId) {
        //参数校验
        if(ObjectUtils.isEmpty(cartInfos)|| StringUtils.isEmpty(userId)){
            return;
        }
        //查看数据库中是否已有该购物车条目
        for (CartInfo cartInfo : cartInfos) {
            CartInfo cartInfoDb = cartInfoMapper.selectOne(new LambdaQueryWrapper<CartInfo>()
                    .eq(CartInfo::getSkuId, cartInfo.getSkuId())
                    .eq(CartInfo::getUserId, userId));
            //如果为空则直接添加
            if(cartInfoDb==null){
                addCart(cartInfo.getSkuId(),cartInfo.getSkuNum(),userId);
            }else {
                //不为空则合并购物车
                cartInfoDb.setSkuNum(cartInfoDb.getSkuNum()+cartInfo.getSkuNum());
                //更新到数据库
                cartInfoMapper.updateById(cartInfoDb);
                //更新到redis
                redisTemplate.boundHashOps("cart:"+userId+":info").put(cartInfoDb.getSkuId()+"",cartInfoDb);
            }
        }
    }

    /**
     * 获取用户选中的购物车列表以及相应的数量和价格
     *
     * @param username
     * @return
     */
    @Override
    public Map<String, Object> getOrderComfirmInfo(String username) {
        //查询选中的购物车列表
        List<CartInfo> cartInfos = cartInfoMapper.selectList(new LambdaQueryWrapper<CartInfo>()
                                                                        .eq(CartInfo::getUserId, username)
                                                                        .eq(CartInfo::getIsChecked, 1));
        //获取总价格和总数量
        Integer totalNum =0;
        BigDecimal totalMoney = new BigDecimal("0");
        for (CartInfo cartInfo : cartInfos) {
            //获取商品数量
            Integer skuNum = cartInfo.getSkuNum();
            totalNum+=skuNum;
            //获取商品实时价格
            Long skuId = cartInfo.getSkuId();
            BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
            totalMoney= totalMoney.add(skuPrice.multiply(new BigDecimal(skuNum + "")));
        }
        //初始化返回结果
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("cartInfos",cartInfos);
        resultMap.put("totalNum",totalNum);
        resultMap.put("totalMoney",totalMoney);
        return resultMap;
    }

    /**
     * 查询用户选中的购物车列表
     *
     * @param username
     * @return
     */
    @Override
    public List<CartInfo> getCheckedCartInfos(String username) {
        List<CartInfo> cartInfos = cartInfoMapper.selectList(new LambdaQueryWrapper<CartInfo>()
                                                                    .eq(CartInfo::getUserId, username)
                                                                    .eq(CartInfo::getIsChecked, 1));
        return cartInfos;
    }

    /**
     * 添加订单后删除选中的购物城
     *
     * @param username
     */
    @Override
    public void deleteCartListAfterOrderAdd(String username) {
        if(StringUtils.isEmpty(username)){
            return;
        }
        //删除选中的购物城
        cartInfoMapper.delete(new LambdaQueryWrapper<CartInfo>().eq(CartInfo::getUserId, username)
                .eq(CartInfo::getIsChecked, 1));
        //查询未选中的购物车
        List<CartInfo> cartInfos = cartInfoMapper.selectList(new LambdaQueryWrapper<CartInfo>().eq(CartInfo::getUserId, username)
                .eq(CartInfo::getIsChecked, 0));
        //删除redis中的缓存
        redisTemplate.delete("cart:"+username+"info");
        //补全未选中的购物车缓存
        for (CartInfo cartInfo : cartInfos) {
            redisTemplate.boundHashOps("cart:"+username+"info").put(cartInfo.getSkuId()+"",cartInfo);
        }


    }
}
