package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import com.atguigu.gmall.product.service.BaseTrademarkService;
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
public class BaseTrademarkServiceImpl extends ServiceImpl<BaseTrademarkMapper, BaseTrademark> implements BaseTrademarkService {

    /**
     * 根据条件查询
     *
     * @param baseTrademark
     * @return
     */
    @Override
    public List<BaseTrademark> findByCriteria(BaseTrademark baseTrademark) {
        if(ObjectUtils.isEmpty(baseTrademark)){
            return baseMapper.selectList(null);
        }
        LambdaQueryWrapper<BaseTrademark> lambdaQueryWrapper = buildLambdaQueryWrapper(baseTrademark);
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
    public IPage<BaseTrademark> selectPage(Integer page, Integer size) {

        if(ObjectUtils.isEmpty(page)){
            page =1;
        }
        if(ObjectUtils.isEmpty(size)){
            size=10;
        }
        Page<BaseTrademark> baseTrademarkPage = new Page<>(page, size);
        return baseMapper.selectPage(baseTrademarkPage,null);
    }

    /**
     * 分页条件查询
     *
     * @param page
     * @param size
     * @param baseTrademark
     * @return
     */
    @Override
    public IPage<BaseTrademark> selectPageByCriteria(Integer page, Integer size, BaseTrademark baseTrademark) {

        return baseMapper.selectPage(new Page<BaseTrademark>(page,size),buildLambdaQueryWrapper(baseTrademark));
    }

    private LambdaQueryWrapper<BaseTrademark> buildLambdaQueryWrapper(BaseTrademark baseTrademark) {
        LambdaQueryWrapper<BaseTrademark> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(!ObjectUtils.isEmpty(baseTrademark.getId()),BaseTrademark::getId,baseTrademark.getId())
                            .eq(!StringUtils.isEmpty(baseTrademark.getLogoUrl()),BaseTrademark::getLogoUrl,baseTrademark.getLogoUrl())
                            .like(!StringUtils.isEmpty(baseTrademark.getTmName()),BaseTrademark::getTmName,baseTrademark.getTmName());
        return lambdaQueryWrapper;
    }
}
