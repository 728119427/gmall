package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class BaseCategory1ServiceImpl extends ServiceImpl<BaseCategory1Mapper, BaseCategory1> implements BaseCategory1Service {

    /**
     * 根据条件查询一级分类
     *
     * @param baseCategory1
     * @return
     */
    @Override
    public List<BaseCategory1> findByCriteria(BaseCategory1 baseCategory1) {
        if(ObjectUtils.isEmpty(baseCategory1)){
            return baseMapper.selectList(null);
        }
        LambdaQueryWrapper<BaseCategory1> lambdaQueryWrapper = buildQueryWrapper(baseCategory1);
        return baseMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseCategory1> selectPage(Integer page, Integer size) {
        //校验参数
        if(page==null){
            page =1;
        }
        if(size==null){
            size=10;
        }
        Page<BaseCategory1> baseCategory1Page = new Page<>(page, size);
        return baseMapper.selectPage(baseCategory1Page,null);
    }

    /**
     * 根据条件分页查询
     *
     * @param page
     * @param size
     * @param baseCategory1
     * @return
     */
    @Override
    public IPage<BaseCategory1> selectPageByCriteira(Integer page, Integer size, BaseCategory1 baseCategory1) {
        LambdaQueryWrapper<BaseCategory1> lambdaQueryWrapper=buildQueryWrapper(baseCategory1);
        Page<BaseCategory1> baseCategory1Page = new Page<>(page, size);
        return baseMapper.selectPage(baseCategory1Page,lambdaQueryWrapper);
    }

    private LambdaQueryWrapper<BaseCategory1> buildQueryWrapper(BaseCategory1 baseCategory1) {
        LambdaQueryWrapper<BaseCategory1> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(baseCategory1.getId()!=null,BaseCategory1::getId,baseCategory1.getId())
                .like(!StringUtils.isEmpty(baseCategory1.getName()),BaseCategory1::getName,baseCategory1.getName());
        return lambdaQueryWrapper;

    }


}
