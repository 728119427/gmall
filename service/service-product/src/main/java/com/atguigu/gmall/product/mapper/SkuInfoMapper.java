package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface SkuInfoMapper extends BaseMapper<SkuInfo> {
    /**
     * 扣减库存
     * @param skuId
     * @param skuNum
     */
    int updateStore(@Param("skuId") String skuId,@Param("skuNum") String skuNum);

    /**
     * 回滚库存
     * @param skuId
     * @param skuNum
     * @return
     */
    int rollbackStore(@Param("skuId") String skuId,@Param("skuNum") String skuNum);
}
