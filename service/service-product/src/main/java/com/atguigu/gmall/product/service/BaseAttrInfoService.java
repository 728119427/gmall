package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BaseAttrInfoService extends IService<BaseAttrInfo> {
    /**
     * 根据条件查询产品属性
     * @param baseAttrInfo
     * @return
     */
    List<BaseAttrInfo> findByCriteria(BaseAttrInfo baseAttrInfo);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    IPage<BaseAttrInfo> selectPage(Integer page, Integer size);

    /**
     * 根据条件分页查询
     * @param page
     * @param size
     * @param baseAttrInfo
     * @return
     */
    IPage<BaseAttrInfo> selectPageByCriteira(Integer page, Integer size, BaseAttrInfo baseAttrInfo);
}
