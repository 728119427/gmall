package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BaseCategory3Service extends IService<BaseCategory3> {
    /**
     * 根据条件查询三级分类
     * @param baseCategory3
     * @return
     */
    List<BaseCategory3> findByCriteria(BaseCategory3 baseCategory3);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    IPage<BaseCategory3> selectPage(Integer page, Integer size);

    /**
     * 根据条件分页查询
     * @param page
     * @param size
     * @param baseCategory3
     * @return
     */
    IPage<BaseCategory3> selectPageByCriteira(Integer page, Integer size, BaseCategory3 baseCategory3);
}
