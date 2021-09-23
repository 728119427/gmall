package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {
    /**
     * 查询指定spu的所有sku与销售属性值对应关系
     * @param spuId
     * @return
     */
    List<Map> selectSkuValueIdsMap(@Param("spuId") Long spuId);
}
