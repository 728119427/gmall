package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.cart.util.GmallThreadLocalUtils;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.context.SaveContextOnUpdateOrErrorResponseWrapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 购物车管理控制层
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartInfoService cartInfoService;

    /**
     * 添加新的购物车条目
     * @param skuId
     * @param num
     * @return
     */
    @PostMapping("/addCart")
    public Result<CartInfo> addCart(@RequestParam("skuId") Long skuId,@RequestParam("num") Integer num){
        String userName = GmallThreadLocalUtils.getUserName();
        CartInfo cartInfo = cartInfoService.addCart(skuId, num, userName);
        return Result.ok(cartInfo);
    }

    /**
     * 修改购物车
     * @param skuId
     * @param num
     * @return
     */
    @PutMapping("/updateCart")
    public Result updateCart(@RequestParam("skuId") Long skuId,@RequestParam("num") Integer num){
        String userName = GmallThreadLocalUtils.getUserName();
        cartInfoService.updateCart(skuId,num,userName);
        return Result.ok();
    }

    /**
     * 删除购物车条目
     * @param skuId
     * @return
     */
    @DeleteMapping("/deleteCart/{skuId}")
    public Result deleteCart(@PathVariable("skuId") Long skuId){
        String userName = GmallThreadLocalUtils.getUserName();
        cartInfoService.delCart(skuId,userName);
        return Result.ok();
    }

    /**
     * 修改商品的选中状态
     * @param skuId
     * @param isCheck
     * @return
     */
    @PutMapping("/updateCheckStatus")
    public Result<CartInfo> updateCartCheckStatus(@RequestParam("skuId") Long skuId,@RequestParam("isCheck") Boolean isCheck) {
        String userName = GmallThreadLocalUtils.getUserName();
        CartInfo cartInfo = cartInfoService.updateCartCheckStatus(skuId, userName, isCheck);
        return Result.ok(cartInfo);
    }

    /**
     * 查询用户的购物车信息
     * @return
     */
    @GetMapping("/getCartInfo")
    public Result<List<CartInfo>> getCartInfo(){
        String userName = GmallThreadLocalUtils.getUserName();
        List<CartInfo> cartInfos = cartInfoService.getCartInfo(userName);
        return Result.ok(cartInfos);
    }

    /**
     * 合并购物车
     * @param cartInfos
     */
    @PostMapping("/mergeCart")
    public Result mergeCartInfo(@RequestBody List<CartInfo> cartInfos){
        String userName = GmallThreadLocalUtils.getUserName();
        cartInfoService.mergeCartInfo(cartInfos,userName);
        return Result.ok();
    }

    /**
     * 获取用户选中的购物车列表以及相应的数量和价格
     * @return
     */
    @GetMapping("/getOrderConfirmInfo")
    public Map<String,Object> getOrderConfirmInfo(){
        String userName = GmallThreadLocalUtils.getUserName();
        return cartInfoService.getOrderComfirmInfo(userName);
    }

    /**
     * 获取用户选中的购物车列表
     * @return
     */
    @GetMapping("/getCheckedCartInfos")
    public List<CartInfo> getCheckedCartInfos(){
        String userName = GmallThreadLocalUtils.getUserName();
        return cartInfoService.getCheckedCartInfos(userName);
    }

    /**
     * 删除选中的购物车列表
     * @return
     */
    @DeleteMapping("/deleteCartList")
    public Result deleteCartList(){
        String userName = GmallThreadLocalUtils.getUserName();
        cartInfoService.deleteCartListAfterOrderAdd(userName);
        return Result.ok();
    }
}
