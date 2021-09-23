package com.atguigu.gmall.user.feign;

import com.atguigu.gmall.model.user.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-user",path = "/api/user")
public interface UserFeignClient {

    /**
     * 根据用户名查找用户
     * @param username
     * @return
     */
    @GetMapping("/getUserInfo/{username}")
    public UserInfo getUserInfoByUsername(@PathVariable String username);
}
