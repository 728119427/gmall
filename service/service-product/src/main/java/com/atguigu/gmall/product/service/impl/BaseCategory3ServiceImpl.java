package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.service.BaseCategory3Service;
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
public class BaseCategory3ServiceImpl extends ServiceImpl<BaseCategory3Mapper, BaseCategory3> implements BaseCategory3Service {

    /**
     * 根据条件查询三级分类
     *
     * @param baseCategory3
     * @return
     */
    @Override
    public List<BaseCategory3> findByCriteria(BaseCategory3 baseCategory3) {
        if(ObjectUtils.isEmpty(baseCategory3)){
            return baseMapper.selectList(null);
        }
        LambdaQueryWrapper<BaseCategory3> lambdaQueryWrapper = buildQueryWrapper(baseCategory3);
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
    public IPage<BaseCategory3> selectPage(Integer page, Integer size) {
        //校验参数
        if(page==null){
            page =1;
        }
        if(size==null){
            size=10;
        }
        Page<BaseCategory3> baseCategory3Page = new Page<>(page, size);
        return baseMapper.selectPage(baseCategory3Page,null);
    }

    /**
     * 根据条件分页查询
     *
     * @param page
     * @param size
     * @param baseCategory3
     * @return
     */
    @Override
    public IPage<BaseCategory3> selectPageByCriteira(Integer page, Integer size, BaseCategory3 baseCategory3) {
        LambdaQueryWrapper<BaseCategory3> lambdaQueryWrapper=buildQueryWrapper(baseCategory3);
        Page<BaseCategory3> baseCategory3Page = new Page<>(page, size);
        return baseMapper.selectPage(baseCategory3Page,lambdaQueryWrapper);
    }

    private LambdaQueryWrapper<BaseCategory3> buildQueryWrapper(BaseCategory3 baseCategory3) {
        LambdaQueryWrapper<BaseCategory3> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(baseCategory3.getId()!=null, BaseCategory3::getId,baseCategory3.getId())
                            .eq(baseCategory3.getCategory2Id()!=null,BaseCategory3::getCategory2Id,baseCategory3.getCategory2Id())
                            .like(!StringUtils.isEmpty(baseCategory3.getName()), BaseCategory3::getName,baseCategory3.getName());
        return lambdaQueryWrapper;

    }



}
