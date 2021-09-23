package com.atguigu.gmall.list.service;

import java.util.Map;

/**
 * 商品搜索服务接口
 */
public interface SearchService {
    /**
     * 根据条件查询商品
     * @param searchMap
     * @return
     */
    Map<String,Object> search(Map<String,String> searchMap);
}
