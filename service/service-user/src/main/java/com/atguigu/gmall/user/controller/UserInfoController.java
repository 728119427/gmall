package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.service.UserInfoService;
import com.atguigu.gmall.user.util.GmallThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.PublicKey;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    @GetMapping("/getUserInfo/{username}")
    public UserInfo getUserInfoByUsername(@PathVariable String username){
        return userInfoService.getUserInfoByUsername(username);
    }

    @GetMapping("/test")
    public String test(){
        return "success";
    }

    @GetMapping("/getUserAddress")
    public Result<List<UserAddress>> getUserAddress(){
        String userName = GmallThreadLocalUtils.getUserName();
        List<UserAddress> userAddress = userInfoService.getUserAddress(userName);
        return Result.ok(userAddress);
    }

    @GetMapping("/sayHello")
    public Result<String> sayHello(){
        return Result.ok("hello!");
    }
}
