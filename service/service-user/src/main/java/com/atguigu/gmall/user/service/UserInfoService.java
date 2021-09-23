package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserInfoService extends IService<UserInfo> {

    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */
    UserInfo getUserInfoByUsername(String username);

    /**
     * 查询用户的收货地址信息
     * @param username
     * @return
     */
    public List<UserAddress> getUserAddress(String username);
}
