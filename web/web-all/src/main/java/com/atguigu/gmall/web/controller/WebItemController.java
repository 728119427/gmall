package com.atguigu.gmall.web.controller;

import com.alibaba.csp.sentinel.command.handler.GetParamFlowRulesCommandHandler;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.feign.ItemFeignClient;
import com.atguigu.gmall.list.client.ListFeignClient;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.product.feign.ProductFeignClient;
import com.atguigu.gmall.web.service.CreateSkuHtmlService;
import com.atguigu.gmall.web.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/web/item")
public class WebItemController {

    @Autowired
    private CreateSkuHtmlService createSkuHtmlService;
    @Autowired
    private ListFeignClient listFeignClient;


 /*   @Autowired
    private ItemFeignClient itemFeignClient;

    @GetMapping("/{skuId}")
    public String getSkuDetail(Model model,@PathVariable("skuId") Long skuId){
        Result<Map> skuDetail = itemFeignClient.getSkuDetail(skuId);
        model.addAttribute("goodsItem",skuDetail);
        return "item/item";
    }
    */

    @Autowired
    private ProductFeignClient productFeignClient;
    /**
     * 打开首页
     */
    @GetMapping("/index")
    public String index(Model model){
        List<JSONObject> baseCategoryList = productFeignClient.getBaseCategoryList();
        model.addAttribute("list",baseCategoryList);
     return "index/index";
    }

    /**
     * 生成商品详情静态页面
     * @param skuId
     * @return
     */
    @ResponseBody
    @GetMapping("/create/{skuId}")
    private String createStaticHtml(@PathVariable("skuId") Long skuId){
        createSkuHtmlService.createSkuHtml(skuId);
        return "success";
    }


    /**
     * 打开商品搜索页面
     * @param model
     * @param searchMap
     * @return
     */
    @GetMapping("/list")
    public String list(Model model, @RequestParam Map<String,String> searchMap, HttpServletRequest request){
        Map<String, Object> searchResult = listFeignClient.search(searchMap);
        model.addAllAttributes(searchResult);
        //查询条件回显
        model.addAttribute("searchMap",searchMap);
        //获取查询url
        String url = getUrl(searchMap,request);
        model.addAttribute("url",url);
        //从查询结果中获取分页数据
        Object pageSize = searchResult.get("pageSize");
        Object pageNum = searchResult.get("pageNum");
        Object totals = searchResult.get("totals");
        //构建分页对象
        Page page = new Page(Long.parseLong(totals.toString()), Integer.parseInt(pageNum.toString()), Integer.parseInt(pageSize.toString()));
        model.addAttribute("page",page);
        return "list/list";
    }

    /**
     * 拼接查询url
     * @param searchMap
     * @param request
     * @return
     */
    private String getUrl(Map<String, String> searchMap, HttpServletRequest request) {
        String url = "http://localhost:8300/web/item/list?";
        for (Map.Entry<String, String> entry : searchMap.entrySet()) {
            if(!entry.getKey().equals("sortField")&&!entry.getKey().equals("sortRule")&&!entry.getKey().equals("pageNum")){
                url+=entry.getKey()+"="+entry.getValue()+"&";
            }

        }
        url=url.substring(0,url.length()-1);
        return url;
    }


}
