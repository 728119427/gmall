package com.atguigu.gmall.oauth.service;

import com.atguigu.gmall.oauth.util.AuthToken;
import io.micrometer.core.ipc.http.HttpSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 用户登录
     * @param username
     * @param passwd
     * @return
     */
    @Override
    public AuthToken login(String username, String passwd) {
        //参数校验
        if(StringUtils.isEmpty(username)||StringUtils.isEmpty(passwd)){
            return null;
        }
        //定义header
        MultiValueMap<String,String> header = new HttpHeaders();
        header.add("Authorization",getHeadParam());
        //定义请求体
        //Map<String,String> body = new HashMap<>();
        MultiValueMap<String,String> body = new HttpHeaders();
        body.add("grant_type","password");
        body.add("username",username);
        body.add("password",passwd);
        //封装参数
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, header);
        //获取请求url
        ServiceInstance instance = loadBalancerClient.choose("service-oauth");
        String url = instance.getUri() +"/oauth/token";
        //发送请求
        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);
        Map exchangeBody = exchange.getBody();
        if(ObjectUtils.isEmpty(exchangeBody)){
            return null;
        }
        //封装请求结果AuthToken
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(exchangeBody.get("access_token").toString());
        authToken.setRefreshToken(exchangeBody.get("refresh_token").toString());
        authToken.setJti(exchangeBody.get("jti").toString());
        //返回结果
        return authToken;
    }


    @Value("${auth.clientId}")
    private String clientId;
    @Value("${auth.clientSecret}")
    private String clientSecret;
    /**
     * 拼接请求头的value
     * @return
     */
    private String getHeadParam() {
        String  headerValue = clientId+":"+clientSecret;
        byte[] encodeValue = Base64Utils.encode(headerValue.getBytes());
        return "Basic "+new String(encodeValue, StandardCharsets.UTF_8);
    }
}
