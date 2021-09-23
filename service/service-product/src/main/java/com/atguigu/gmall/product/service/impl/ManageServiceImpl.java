package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.ProductConst;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.atguigu.gmall.rabbit.config.RabbitService;
import com.atguigu.gmall.rabbit.constant.MqConst;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class ManageServiceImpl implements ManageService {
    @Autowired
    private BaseCategory1Mapper baseCategory1Mapper;
    @Autowired
    private BaseCategory2Mapper baseCategory2Mapper;
    @Autowired
    private BaseCategory3Mapper baseCategory3Mapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;
    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;
    @Autowired
    private SpuInfoMapper spuInfoMapper;
    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;
    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;
    @Autowired
    private SpuImageMapper spuImageMapper;
    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;
    @Autowired
    private SkuInfoMapper skuInfoMapper;
    @Autowired
    private SkuImageMapper skuImageMapper;
    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;


    /**
     * 查询所有一级分类列表
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getCategory1List() {
        return baseCategory1Mapper.selectList(null);
    }

    /**
     * 根据一级分类id查询二级分类
     *
     * @param category1Id
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory2ListByCategory1Id(Long category1Id) {
        //校验参数
        if(ObjectUtils.isEmpty(category1Id)){
            throw new RuntimeException("查询二级分类列表失败,一级分类id不能为空");
        }
        LambdaQueryWrapper<BaseCategory2> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BaseCategory2::getCategory1Id,category1Id);
        return baseCategory2Mapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据二级分类id查询三级分类
     *
     * @param category2Id
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory3ListByCategory2Id(Long category2Id) {
        //校验参数
        if(ObjectUtils.isEmpty(category2Id)){
            throw new RuntimeException("查询三级分类列表失败,二级分类id不能为空");
        }
        LambdaQueryWrapper<BaseCategory3> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BaseCategory3::getCategory2Id,category2Id);
        return baseCategory3Mapper.selectList(lambdaQueryWrapper);
    }

    /**
     * 根据分类查询所有规格信息列表：查询1，2，3级分类的规格信息列表，若只查询某一分类的规格信息，其他分类传入0
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrInfoListByCategoryId(Long category1Id, Long category2Id, Long category3Id) {
        return baseAttrInfoMapper.selectListByCategoryId(category1Id,category2Id,category3Id);
    }

    /**
     * 新增规格
     *
     * @param baseAttrInfo
     * @return
     */
    @Override
    public BaseAttrInfo saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        if(ObjectUtils.isEmpty(baseAttrInfo)){
            throw new RuntimeException("添加规格不能为空！");
        }
        if(baseAttrInfo.getId()==null){
            //新增规格
            baseAttrInfoMapper.insert(baseAttrInfo);
        }else {
            //修改规格
            baseAttrInfoMapper.updateById(baseAttrInfo);
            //删除该info所对应的旧valueList
            LambdaQueryWrapper<BaseAttrValue> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(BaseAttrValue::getAttrId,baseAttrInfo.getId());
            baseAttrValueMapper.delete(queryWrapper);
        }
        //新增info对应的valueList
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        attrValueList = attrValueList.stream().map((baseAttrValue -> {
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.insert(baseAttrValue);
            return baseAttrValue;
        })).collect(Collectors.toList());

        baseAttrInfo.setAttrValueList(attrValueList);
        return baseAttrInfo;

    }

    /**
     * 根据平台属性名称的id查询平台属性的值列表
     *
     * @param id
     * @return
     */
    @Override
    public List<BaseAttrValue> getAttrValueList(Long id) {
        LambdaQueryWrapper<BaseAttrValue> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(BaseAttrValue::getAttrId,id);
        return baseAttrValueMapper.selectList(lambdaQueryWrapper);
    }


    /**
     * 分页查询spuInfo的信息
     *
     * @param category3Id
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<SpuInfo> selectSpuInfoList(Long category3Id, Integer page, Integer size) {
        if(ObjectUtils.isEmpty(category3Id)){
            throw new RuntimeException("分类id不能为空！");
        }
        if(page==null || page<=0){
            page=1;
        }
        if(size==null || size<=0){
            size=10;
        }

        LambdaQueryWrapper<SpuInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SpuInfo::getCategory3Id,category3Id);
        IPage<SpuInfo> spuInfoIPage = spuInfoMapper.selectPage(new Page<SpuInfo>(page, size), queryWrapper);
        return spuInfoIPage;
    }

    /**
     * 分页查询品牌信息
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<BaseTrademark> getBaseTrademarkPageList(Integer page, Integer size) {
        //校验参数
        if(page==null || page<=0){
            page =1;
        }

        if(size==null || size <= 0){
            size=10;
        }
        //分页查询
        return baseTrademarkMapper.selectPage(new Page<BaseTrademark>(page,size),null);
    }

    /**
     * 查询所有销售属性信息
     *
     * @return
     */
    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }

    /**
     * 查询所有品牌信息
     *
     * @return
     */
    @Override
    public List<BaseTrademark> getTrademarkList() {
        List<BaseTrademark> baseTrademarks = baseTrademarkMapper.selectList(null);
        return baseTrademarks;
    }

    /**
     * 保存spuInfo
     *
     * @param spuInfo
     * @return
     */
    @Override
    public SpuInfo saveSpuInfo(SpuInfo spuInfo) {
        //校验参数
        if(ObjectUtils.isEmpty(spuInfo)){
            throw new RuntimeException("spu不能为空！");
        }
        Long spuInfoId = spuInfo.getId();
        if(ObjectUtils.isEmpty(spuInfoId)){
            //新增spu
            spuInfoMapper.insert(spuInfo);
            spuInfoId = spuInfo.getId();
        }else{
            //更新spuInfo并删除相关旧值
            spuInfoMapper.updateById(spuInfo);
            //删除spuImage，spuAttr,spuAttrValue
            spuImageMapper.delete(new LambdaQueryWrapper<SpuImage>().eq(SpuImage::getSpuId,spuInfoId));
            spuSaleAttrMapper.delete(new LambdaQueryWrapper<SpuSaleAttr>().eq(SpuSaleAttr::getSpuId,spuInfoId));
            spuSaleAttrValueMapper.delete(new LambdaQueryWrapper<SpuSaleAttrValue>().eq(SpuSaleAttrValue::getSpuId,spuInfoId));
        }
        //新增spuImage
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        setAndSaveSpuImgList(spuInfo,spuImageList);

        //新增spuAttr
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
       setAndSaveSpuSaleAttrList(spuInfo,spuSaleAttrList);

        return spuInfo;
    }

    /**
     * 保存并设置spuImageList
     * @param spuInfo
     * @param spuImageList
     */
    private void setAndSaveSpuImgList(SpuInfo spuInfo,List<SpuImage> spuImageList){
        List<SpuImage> imageList = spuImageList.stream().peek(img -> {
            img.setSpuId(spuInfo.getId());
            spuImageMapper.insert(img);
        }).collect(Collectors.toList());
        //设置新值
        spuInfo.setSpuImageList(imageList);
    }

    /**
     * 保存并设置spuSaleAttrList
     * @param spuInfo
     * @param spuSaleAttrList
     */
    private void setAndSaveSpuSaleAttrList(SpuInfo spuInfo,List<SpuSaleAttr> spuSaleAttrList){
        List<SpuSaleAttr> saleAttrList = spuSaleAttrList.stream().peek(spuSaleAttr -> {
            spuSaleAttr.setSpuId(spuInfo.getId());
            spuSaleAttrMapper.insert(spuSaleAttr);
            //新增spuAttrValue
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            List<SpuSaleAttrValue> saleAttrValueList = spuSaleAttrValueList.stream().peek(spuSaleAttrValue -> {
                spuSaleAttrValue.setSpuId(spuInfo.getId());
                spuSaleAttrValue.setBaseSaleAttrId(spuSaleAttr.getId());
                spuSaleAttrValueMapper.insert(spuSaleAttrValue);
            }).collect(Collectors.toList());
            spuSaleAttr.setSpuSaleAttrValueList(saleAttrValueList);
        }).collect(Collectors.toList());
        //设置新值
        spuInfo.setSpuSaleAttrList(saleAttrList);
    }

    /**
     * 根据spuId查询
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuImage> getSpuImageListBySpuId(Long spuId) {
        LambdaQueryWrapper<SpuImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SpuImage::getSpuId,spuId);
        return spuImageMapper.selectList(queryWrapper);
    }

    /**
     * 根据spuId查询spu的销售属性列表
     *
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListBySpuId(Long spuId) {
        return spuSaleAttrMapper.selectSpuAttrListBySpuId(spuId);
    }

    /**
     * 保存sku信息
     *
     * @param skuInfo
     * @return
     */
    @Override
    public SkuInfo saveSkuInfo(SkuInfo skuInfo) {
        if(ObjectUtils.isEmpty(skuInfo)){
            throw new RuntimeException("sku不能为空");
        }
        Long skuId = skuInfo.getId();
        if(skuId==null){
            //新增sku
            skuInfoMapper.insert(skuInfo);
            //给skuId赋值
            skuId=skuInfo.getId();
        }else {
            //更新sku
            skuInfoMapper.updateById(skuInfo);
            //删除旧的skuImage,skuAttrValue,skuSaleAttrValue
            skuImageMapper.delete(new LambdaQueryWrapper<SkuImage>().eq(SkuImage::getSkuId,skuId));
            skuAttrValueMapper.delete(new LambdaQueryWrapper<SkuAttrValue>().eq(SkuAttrValue::getSkuId,skuId));
            skuSaleAttrValueMapper.delete(new LambdaQueryWrapper<SkuSaleAttrValue>().eq(SkuSaleAttrValue::getSkuId,skuId));
        }
        //新增skuImage
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        skuImageList=saveSkuImage(skuId,skuImageList);
        skuInfo.setSkuImageList(skuImageList);
        //新增skuAttrValue
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        skuAttrValueList= saveSkuAttrValueList(skuId,skuAttrValueList);
        skuInfo.setSkuAttrValueList(skuAttrValueList);
        //新增skuSaleAttrValue
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        skuSaleAttrValueList=saveSkuSaleAttrValueList(skuId,skuInfo.getSpuId(),skuSaleAttrValueList);
        skuInfo.setSkuSaleAttrValueList(skuSaleAttrValueList);

        return skuInfo;
    }

    /**
     * 批量保存sku的销售属性值
     * @param skuId
     * @param skuSaleAttrValueList
     * @return
     */
    private List<SkuSaleAttrValue> saveSkuSaleAttrValueList(Long skuId,Long spuId, List<SkuSaleAttrValue> skuSaleAttrValueList) {
        List<SkuSaleAttrValue> saleAttrValueList = skuSaleAttrValueList.stream().peek(skuSaleAttrValue -> {
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValue.setSpuId(spuId);
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }).collect(Collectors.toList());
        return saleAttrValueList;
    }

    /**
     * 批量保存skuAttrValue
     * @param skuId
     * @param skuAttrValueList
     * @return
     */
    private List<SkuAttrValue> saveSkuAttrValueList(Long skuId, List<SkuAttrValue> skuAttrValueList) {
        List<SkuAttrValue> attrValueList = skuAttrValueList.stream().peek(skuAttrValue -> {
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.insert(skuAttrValue);
        }).collect(Collectors.toList());
        return attrValueList;
    }

    /**
     * 批量保存skuImage
     * @param skuId
     * @param skuImageList
     * @return
     */
    private List<SkuImage> saveSkuImage(Long skuId, List<SkuImage> skuImageList) {
        List<SkuImage> imageList = skuImageList.stream().peek(skuImage -> {
            skuImage.setSkuId(skuId);
            skuImageMapper.insert(skuImage);
        }).collect(Collectors.toList());
        return imageList;
    }

    /**
     * 分页查询sku列表信息
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public IPage<SkuInfo> skuInfoPageList(Integer page, Integer size) {
        //校验参数
        if(page==null || page<=0){
            page=1;
        }
        if(size==null || size<=0){
            size=10;
        }
        return skuInfoMapper.selectPage(new Page<SkuInfo>(page,size),null);
    }

    @Autowired
    private RabbitService rabbitService;

    /**
     * 商品上架或下架
     *
     * @param skuId
     * @param status
     */
    @Override
    public void upOrDown(Long skuId, Short status) {
        //参数校验
        if(skuId==null || skuId<=0){
            throw new RuntimeException("请求参数错误！");
        }
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if(skuInfo==null ){
            throw new RuntimeException("该商品不存在,无法操作！");
        }
        skuInfo.setIsSale(status.intValue());
        skuInfoMapper.updateById(skuInfo);
        //数据同步到es
        if(ProductConst.ON_SALE.equals(status)){
            //商品上架消息
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_GOODS,MqConst.ROUTING_GOODS_UPPER,skuId+"");
        }else {
            //发送商品下架消息
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_GOODS,MqConst.ROUTING_GOODS_LOWER,skuId+"");
        }
    }
}
