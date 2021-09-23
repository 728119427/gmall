package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BaseCategory1Service extends IService<BaseCategory1> {
    /**
     * 根据条件查询一级分类
     * @param baseCategory1
     * @return
     */
    List<BaseCategory1> findByCriteria(BaseCategory1 baseCategory1);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    IPage<BaseCategory1> selectPage(Integer page, Integer size);

    /**
     * 根据条件分页查询
     * @param page
     * @param size
     * @param baseCategory1
     * @return
     */
    IPage<BaseCategory1> selectPageByCriteira(Integer page, Integer size, BaseCategory1 baseCategory1);
}
