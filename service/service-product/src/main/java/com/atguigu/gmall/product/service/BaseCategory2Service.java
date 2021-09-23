package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseCategory2;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BaseCategory2Service extends IService<BaseCategory2> {
    /**
     * 根据条件查询二级分类
     * @param baseCategory2
     * @return
     */
    List<BaseCategory2> findByCriteria(BaseCategory2 baseCategory2);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    IPage<BaseCategory2> selectPage(Integer page, Integer size);

    /**
     * 根据条件分页查询
     * @param page
     * @param size
     * @param baseCategory2
     * @return
     */
    IPage<BaseCategory2> selectPageByCriteira(Integer page, Integer size, BaseCategory2 baseCategory2);
}
