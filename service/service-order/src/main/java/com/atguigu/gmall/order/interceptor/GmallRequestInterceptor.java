package com.atguigu.gmall.order.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Component
public class GmallRequestInterceptor implements RequestInterceptor {
    /**
     * Called for every request. Add data using methods on the supplied {@link RequestTemplate}.
     *
     * @param template
     */
    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes requestAttributes =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(requestAttributes!=null){
            //mq收到消息后调用的feign是内部调用，不需要令牌，所以这里是null也无所谓
            HttpServletRequest request = requestAttributes.getRequest();
            //获取requeset中的所有请求头，放入feign的request中
            Enumeration<String> headerNames = request.getHeaderNames();
            if(!ObjectUtils.isEmpty(headerNames)){
                while (headerNames.hasMoreElements()){
                    String name = headerNames.nextElement();
                    String headerValue = request.getHeader(name);
                    template.header(name,headerValue);
                }
            }
        }

    }
}
