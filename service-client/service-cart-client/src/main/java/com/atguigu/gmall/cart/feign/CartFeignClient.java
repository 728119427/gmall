package com.atguigu.gmall.cart.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "service-cart",path = "/api/cart")
public interface CartFeignClient {

    /**
     * 获取用户选中的购物车列表
     * @return
     */
    @GetMapping("/getCheckedCartInfos")
    public List<CartInfo> getCheckedCartInfos();

    /**
     * 删除选中的购物车列表
     * @return
     */
    @DeleteMapping("/deleteCartList")
    public Result deleteCartList();
}
