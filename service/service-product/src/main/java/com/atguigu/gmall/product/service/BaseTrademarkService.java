package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface BaseTrademarkService extends IService<BaseTrademark> {
    /**
     * 根据条件查询
     * @param baseTrademark
     * @return
     */
    List<BaseTrademark> findByCriteria(BaseTrademark baseTrademark);

    /**
     * 分页查询
     * @param page
     * @param size
     * @return
     */
    IPage<BaseTrademark> selectPage(Integer page, Integer size);

    /**
     * 分页条件查询
     * @param page
     * @param size
     * @param baseTrademark
     * @return
     */
    IPage<BaseTrademark> selectPageByCriteria(Integer page, Integer size, BaseTrademark baseTrademark);
}
