package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.mapper.UserInfoMapper;
import com.atguigu.gmall.user.service.UserInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private UserAddressMapper userAddressMapper;
    /**
     * 根据用户名查询用户信息
     *
     * @param username
     * @return
     */
    @Override
    public UserInfo getUserInfoByUsername(String username) {
        //参数校验
        if(StringUtils.isEmpty(username)){
            return null;
        }
        LambdaQueryWrapper<UserInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserInfo::getLoginName,username);
        UserInfo userInfo = userInfoMapper.selectOne(lambdaQueryWrapper);
        if(userInfo==null || userInfo.getId()==null){
            return null;
        }
        return userInfo;

    }

    /**
     * 查询用户的收货地址信息
     *
     * @param username
     * @return
     */
    @Override
    public List<UserAddress> getUserAddress(String username) {
        //根据username查询userId
        UserInfo userInfo = userInfoMapper.selectOne(new LambdaQueryWrapper<UserInfo>().eq(UserInfo::getLoginName, username));
        return userAddressMapper.selectList(new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId,userInfo.getId()));
    }
}
