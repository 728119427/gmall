package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 分类与属性管理service接口类
 */
public interface ManageService {

    /**
     *  查询所有一级分类列表
     * @return
     */
    List<BaseCategory1> getCategory1List();

    /**
     * 根据一级分类id查询二级分类
     * @param category1Id
     * @return
     */
    List<BaseCategory2> getCategory2ListByCategory1Id(Long category1Id);

    /**
     * 根据二级分类id查询三级分类
     * @param category2Id
     * @return
     */
    List<BaseCategory3> getCategory3ListByCategory2Id(Long category2Id);

    /**
     * 根据分类查询所有规格信息列表：查询1，2，3级分类的规格信息列表，若只查询某一分类的规格信息，其他分类传入0
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<BaseAttrInfo> getBaseAttrInfoListByCategoryId(Long category1Id,Long category2Id,Long category3Id);

    /**
     * 新增规格
     * @param baseAttrInfo
     * @return
     */
    BaseAttrInfo saveAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据平台属性名称的id查询平台属性的值列表
     * @param id
     * @return
     */
    List<BaseAttrValue> getAttrValueList(Long id);


    /**
     * 分页查询spuInfo的信息
     * @param category3Id
     * @param page
     * @param size
     * @return
     */
    IPage<SpuInfo> selectSpuInfoList(Long category3Id,Integer page,Integer size);

    /**
     * 分页查询品牌信息
     * @param page
     * @param size
     * @return
     */
    IPage<BaseTrademark> getBaseTrademarkPageList(Integer page, Integer size);

    /**
     * 查询所有销售属性信息
     * @return
     */
    List<BaseSaleAttr> getBaseSaleAttrList();

    /**
     * 查询所有品牌信息
     * @return
     */
    List<BaseTrademark> getTrademarkList();

    /**
     * 保存spuInfo
     * @param spuInfo
     * @return
     */
    SpuInfo saveSpuInfo(SpuInfo spuInfo);

    /**
     * 根据spuId查询spuImage列表
     * @param spuId
     * @return
     */
    List<SpuImage> getSpuImageListBySpuId(Long spuId);

    /**
     * 根据spuId查询spu的销售属性列表
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrListBySpuId(Long spuId);

    /**
     * 保存sku信息
     * @param skuInfo
     * @return
     */
    SkuInfo saveSkuInfo(SkuInfo skuInfo);

    /**
     * 分页查询sku列表信息
     * @param page
     * @param size
     * @return
     */
    IPage<SkuInfo> skuInfoPageList(Integer page,Integer size);

    /**
     * 商品上架或下架
     * @param skuId
     * @param status
     */
    void upOrDown(Long skuId,Short status);
}
