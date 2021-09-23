package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo> implements BaseAttrInfoService {

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    /**
     * 根据条件查询产品属性
     *
     * @param baseAttrInfo
     * @return
     */
    @Override
    public List<BaseAttrInfo> findByCriteria(BaseAttrInfo baseAttrInfo) {
        if(ObjectUtils.isEmpty(baseAttrInfo)){
            return baseAttrInfoMapper.selectList(null);
        }
        LambdaQueryWrapper<BaseAttrInfo> lambdaQueryWrapper = buildQueryWrapper(baseAttrInfo);
        return baseAttrInfoMapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseAttrInfo> selectPage(Integer page, Integer size) {
        //校验参数
        if(page==null){
            page =1;
        }
        if(size==null){
            size=10;
        }
        Page<BaseAttrInfo> baseAttrInfoPage = new Page<>(page, size);
        return baseAttrInfoMapper.selectPage(baseAttrInfoPage,null);
    }

    /**
     * 根据条件分页查询
     *
     * @param page
     * @param size
     * @param baseAttrInfo
     * @return
     */
    @Override
    public IPage<BaseAttrInfo> selectPageByCriteira(Integer page, Integer size, BaseAttrInfo baseAttrInfo) {
        LambdaQueryWrapper<BaseAttrInfo> lambdaQueryWrapper=buildQueryWrapper(baseAttrInfo);
        Page<BaseAttrInfo> baseAttrInfoPage = new Page<>(page, size);
        return baseAttrInfoMapper.selectPage(baseAttrInfoPage,lambdaQueryWrapper);
    }

    private LambdaQueryWrapper<BaseAttrInfo> buildQueryWrapper(BaseAttrInfo baseAttrInfo) {
        LambdaQueryWrapper<BaseAttrInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(baseAttrInfo.getId()!=null,BaseAttrInfo::getId,baseAttrInfo.getId())
                .eq(!ObjectUtils.isEmpty(baseAttrInfo.getCategoryId()),BaseAttrInfo::getCategoryId,baseAttrInfo.getCategoryId())
                .eq(!ObjectUtils.isEmpty(baseAttrInfo.getCategoryLevel()),BaseAttrInfo::getCategoryLevel,baseAttrInfo.getCategoryLevel())
                .like(!StringUtils.isEmpty(baseAttrInfo.getAttrName()),BaseAttrInfo::getAttrName,baseAttrInfo.getAttrName());
        return lambdaQueryWrapper;

    }


}
