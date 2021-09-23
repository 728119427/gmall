package com.atguigu.gmall.oauth.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class LoginController {
    @Autowired
    private LoginService loginService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/login")
    public Result<AuthToken> login(@RequestParam("username") String username,
                                   @RequestParam("password") String password,
                                   HttpServletRequest request){
        AuthToken authToken = loginService.login(username, password);
        //将登录结果保存到redis
        String ipAddress = IpUtil.getIpAddress(request);
        stringRedisTemplate.boundValueOps("ip:"+ipAddress).set(authToken.getAccessToken());
        return Result.ok(authToken);
    }
}
