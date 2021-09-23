package com.atguigu.gmall.oauth.service;

import com.atguigu.gmall.oauth.util.AuthToken;

/**
 * 登录接口
 */
public interface LoginService {

    /**
     * 用户登录
     * @param username
     * @param passwd
     * @return
     */
    AuthToken login(String username,String passwd);
}
