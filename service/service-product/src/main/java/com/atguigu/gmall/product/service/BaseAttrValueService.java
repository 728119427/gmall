package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BaseAttrValueService extends IService<BaseAttrValue> {
    /**
     * 根据条件查询产品属性的值
     * @param baseAttrValue
     * @return
     */
    List<BaseAttrValue> findByCriteria(BaseAttrValue baseAttrValue);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    IPage<BaseAttrValue> selectPage(Integer page, Integer size);

    /**
     * 根据条件分页查询
     * @param page
     * @param size
     * @param baseAttrValue
     * @return
     */
    IPage<BaseAttrValue> selectPageByCriteira(Integer page, Integer size, BaseAttrValue baseAttrValue);
}
