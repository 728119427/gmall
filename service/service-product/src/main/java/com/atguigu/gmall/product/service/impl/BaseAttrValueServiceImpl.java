package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
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
public class BaseAttrValueServiceImpl extends ServiceImpl<BaseAttrValueMapper, BaseAttrValue> implements BaseAttrValueService {

    /**
     * 根据条件查询产品属性的值
     *
     * @param baseAttrValue
     * @return
     */
    @Override
    public List<BaseAttrValue> findByCriteria(BaseAttrValue baseAttrValue) {
        if(ObjectUtils.isEmpty(baseAttrValue)){
            return baseMapper.selectList(null);
        }
        LambdaQueryWrapper<BaseAttrValue> lambdaQueryWrapper = buildQueryWrapper(baseAttrValue);
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
    public IPage<BaseAttrValue> selectPage(Integer page, Integer size) {
        //校验参数
        if(page==null){
            page =1;
        }
        if(size==null){
            size=10;
        }
        Page<BaseAttrValue> baseAttrValuePage = new Page<>(page, size);
        return baseMapper.selectPage(baseAttrValuePage,null);
    }

    /**
     * 根据条件分页查询
     *
     * @param page
     * @param size
     * @param baseAttrValue
     * @return
     */
    @Override
    public IPage<BaseAttrValue> selectPageByCriteira(Integer page, Integer size, BaseAttrValue baseAttrValue) {
        LambdaQueryWrapper<BaseAttrValue> lambdaQueryWrapper=buildQueryWrapper(baseAttrValue);
        Page<BaseAttrValue> baseAttrValuePage = new Page<>(page, size);
        return baseMapper.selectPage(baseAttrValuePage,lambdaQueryWrapper);
    }

    private LambdaQueryWrapper<BaseAttrValue> buildQueryWrapper(BaseAttrValue baseAttrValue) {
        LambdaQueryWrapper<BaseAttrValue> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(!ObjectUtils.isEmpty(baseAttrValue.getId()),BaseAttrValue::getId,baseAttrValue.getId())
                .eq(!ObjectUtils.isEmpty(baseAttrValue.getAttrId()),BaseAttrValue::getAttrId,baseAttrValue.getAttrId())
                .like(!StringUtils.isEmpty(baseAttrValue.getValueName()),BaseAttrValue::getValueName,baseAttrValue.getValueName());
        return lambdaQueryWrapper;

    }


}
