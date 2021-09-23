package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import com.atguigu.gmall.product.service.BaseCategory2Service;
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
public class BaseCategory2ServiceImpl extends ServiceImpl<BaseCategory2Mapper, BaseCategory2> implements BaseCategory2Service {

    /**
     * 根据条件查询二级分类
     *
     * @param baseCategory2
     * @return
     */
    @Override
    public List<BaseCategory2> findByCriteria(BaseCategory2 baseCategory2) {
        if(ObjectUtils.isEmpty(baseCategory2)){
            return baseMapper.selectList(null);
        }
        LambdaQueryWrapper<BaseCategory2> lambdaQueryWrapper = buildQueryWrapper(baseCategory2);
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
    public IPage<BaseCategory2> selectPage(Integer page, Integer size) {
        //校验参数
        if(page==null){
            page =1;
        }
        if(size==null){
            size=10;
        }
        Page<BaseCategory2> baseCategory2Page = new Page<>(page, size);
        return baseMapper.selectPage(baseCategory2Page,null);
    }

    /**
     * 根据条件分页查询
     *
     * @param page
     * @param size
     * @param baseCategory2
     * @return
     */
    @Override
    public IPage<BaseCategory2> selectPageByCriteira(Integer page, Integer size, BaseCategory2 baseCategory2) {
        LambdaQueryWrapper<BaseCategory2> lambdaQueryWrapper=buildQueryWrapper(baseCategory2);
        Page<BaseCategory2> baseCategory2Page = new Page<>(page, size);
        return baseMapper.selectPage(baseCategory2Page,lambdaQueryWrapper);
    }

    private LambdaQueryWrapper<BaseCategory2> buildQueryWrapper(BaseCategory2 baseCategory2) {
        LambdaQueryWrapper<BaseCategory2> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(baseCategory2.getId()!=null, BaseCategory2::getId,baseCategory2.getId())
                            .eq(baseCategory2.getCategory1Id()!=null,BaseCategory2::getCategory1Id,baseCategory2.getCategory1Id())
                            .like(!StringUtils.isEmpty(baseCategory2.getName()), BaseCategory2::getName,baseCategory2.getName());
        return lambdaQueryWrapper;

    }


}
