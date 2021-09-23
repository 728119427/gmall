package com.atguigu.gmall.filter;

import com.atguigu.gmall.common.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
@Component
public class GmallGlobalFilter implements GlobalFilter, Ordered {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest request = exchange.getRequest();
        //获取请求地址，判断是否登录请求
        String path = request.getURI().getPath();
        if(path.contains("/login")){
            //登录请求直接放行
            return chain.filter(exchange);
        }
        //从请求参数，header，cookie中查找token
        String token = request.getQueryParams().getFirst("token");
        if(StringUtils.isEmpty(token)){
            //从header中找
            token = request.getHeaders().getFirst("token");
            if(StringUtils.isEmpty(token)){
                //从cookie中找
                MultiValueMap<String, HttpCookie> cookies = request.getCookies();
                if(cookies!=null && cookies.size()>0&& cookies.getFirst("token")!=null){
                    token=cookies.getFirst("token").getValue();
                }
            }
        }
        //未获取到token
        if(StringUtils.isEmpty(token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //获取到token,判断ip是否正确
        String gatwayIpAddress = IpUtil.getGatwayIpAddress(request);
        String oldToken = stringRedisTemplate.boundValueOps("ip:" + gatwayIpAddress).get();
        if(!token.equals(oldToken)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //将token重新放入请求头
        request.mutate().header("Authorization", "Bearer " + token);
        //放行
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
