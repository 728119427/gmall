package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.list.dao.GoodsRepository;
import com.atguigu.gmall.list.service.GoodsService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ProductFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Value("${es.hotScore.incrStep}")
    private Integer incrStep;
    @Value("${es.hotScore.syncLevel}")
    private Integer syncLevel;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 商品上架
     *
     * @param skuId
     * @return
     */
    @Override
    public Goods upperGoods(Long skuId) {
        //参数校验
        if(skuId==null){
            return null;
        }
        //初始化商品
        Goods goods = new Goods();
        //查询sku商品信息
        SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
        if(ObjectUtils.isEmpty(skuInfo)){
            return null;
        }
        //设置商品基本信息
        goods.setId(skuInfo.getId());
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        goods.setCreateTime(new Date());
        goods.setTitle(skuInfo.getSkuName());
        //查询商品价格信息
        BigDecimal skuPrice = productFeignClient.getSkuPrice(skuId);
        goods.setPrice(skuPrice.doubleValue());
        //查询商品分类信息
        BaseCategoryView baseCategoryView = productFeignClient.getBaseCategoryView(skuInfo.getCategory3Id());
        goods.setCategory1Id(baseCategoryView.getCategory1Id());
        goods.setCategory1Name(baseCategoryView.getCategory1Name());
        goods.setCategory2Id(baseCategoryView.getCategory2Id());
        goods.setCategory2Name(baseCategoryView.getCategory2Name());
        goods.setCategory3Id(baseCategoryView.getCategory3Id());
        goods.setCategory3Name(baseCategoryView.getCategory3Name());
        //查询商品的品牌信息
        BaseTrademark baseTrademark = productFeignClient.getBaseTrademarkById(skuInfo.getTmId());
        goods.setTmId(baseTrademark.getId());
        goods.setTmName(baseTrademark.getTmName());
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        //查询商品的平台属性
        List<BaseAttrInfo> baseAttrInfoList = productFeignClient.getBaseAttrInfoList(skuId);
        List<SearchAttr> searchAttrs = baseAttrInfoList.stream().map(baseAttrInfo -> {
            SearchAttr searchAttr = new SearchAttr();
            searchAttr.setAttrId(baseAttrInfo.getId());
            searchAttr.setAttrName(baseAttrInfo.getAttrName());
            searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
            return searchAttr;
        }).collect(Collectors.toList());
        goods.setAttrs(searchAttrs);
        //设置热度
        goods.setHotScore(0L);
        //添加到es，并返回商品信息
        Goods save = goodsRepository.save(goods);
        return save;
    }

    /**
     * 商品下架
     *
     * @param skuId
     */
    @Override
    public void lowerGoods(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    /**
     * 增加商品的热度值
     *
     * @param skuId
     */
    @Override
    public Long incrHotScore(Long skuId) {
        //参数校验
        if(skuId==null){
            return null;
        }
        //从es中查询数据
        Goods goods = goodsRepository.findById(skuId).get();
        if(goods==null){
            return null;
        }
        //往redis中设置热度值,每次加一
        long score = stringRedisTemplate.boundZSetOps(RedisConst.HOT_SCORE).incrementScore(RedisConst.SKUKEY_PREFIX + skuId, incrStep).longValue();
        if(score%syncLevel==0){
            //同步热度值到es
            goods.setHotScore(score);
            goodsRepository.save(goods);
        }
        return score;
    }
}
