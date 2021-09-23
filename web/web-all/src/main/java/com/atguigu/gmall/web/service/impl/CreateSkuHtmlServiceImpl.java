package com.atguigu.gmall.web.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.feign.ItemFeignClient;
import com.atguigu.gmall.product.feign.ProductFeignClient;
import com.atguigu.gmall.web.service.CreateSkuHtmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Service
public class CreateSkuHtmlServiceImpl implements CreateSkuHtmlService {

    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private ItemFeignClient itemFeignClient;

    /**
     * 生成商品详情静态页面
     *
     * @param skuId
     */
    @Override
    public void createSkuHtml(Long skuId) {
        //校验参数
        if(ObjectUtils.isEmpty(skuId)){
            return;
        }
        //获取商品详情
        Map goodItems = itemFeignClient.getSkuDetail(skuId).getData();
        //获取模板
        String template ="item/item";
        //获取上下文,添加商品详情
        Context context = new Context();
        context.setVariables(goodItems);
        try {
            //获取输出流
            File file = new File("f://test/", skuId + ".html");
            PrintWriter printWriter = new PrintWriter(file,"utf-8");
            //生成静态页面
            templateEngine.process(template,context,printWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
