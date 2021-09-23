package com.atguigu.gmall.product.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 查询商品详情相关的接口的实现类
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ItemServiceImpl implements ItemService {
    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private SkuImageMapper skuImageMapper;
    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;
    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private BaseTrademarkMapper baseTrademarkMapper;
    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    /**
     * 根据skuId查询skuInfo
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
       return skuInfoMapper.selectById(skuId);
    }


    /**
     * 根据catogory3Id查询baseCategoryView
     *
     * @param category3Id
     * @return
     */
    @Override
    public BaseCategoryView getBaseCategoryViewByCategory3Id(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    /**
     * 查询sku的价格
     *
     * @param skuId
     * @return
     */
    @Override
    public BigDecimal getPrice(Long skuId) {

        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        if(ObjectUtils.isEmpty(skuInfo)){
            return new BigDecimal("0");
        }
        return skuInfo.getPrice();
    }

    /**
     * 根据spuId和skuId查询商品的销售属性
     *
     * @param skuId
     * @param spuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(Long skuId, Long spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuId,spuId);
    }

    /**
     * 查询指定spu的所有销售属性值与sku对应关系
     *
     * @param spuId
     * @return
     */
    @Override
    public Map getSkuValueIdsMap(Long spuId) {
        List<Map> maps = skuSaleAttrValueMapper.selectSkuValueIdsMap(spuId);
        Map map = new HashMap();
        for (Map map1 : maps) {
            map.put(map1.get("sku_id"),map1.get("value_id"));
        }
        return map;
    }

    /**
     * 查询sku的图片列表
     *
     * @param skuId
     * @return
     */
    @Override
    public List<SkuImage> getSkuImages(Long skuId) {
        LambdaQueryWrapper<SkuImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuImage::getSkuId, skuId);
        return skuImageMapper.selectList(queryWrapper);
    }


    /**
     * 查询缓存并手动实现redis分布式锁
     * @param skuId
     * @return
     */
    public SkuInfo getSkuInfoLockManual(Long skuId){
        //参数校验
        if(ObjectUtils.isEmpty(skuId)){
            return null;
        }
        //定义key
        String key = RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKUKEY_SUFFIX;
        //从缓存中获取数据
        Object skuInfo =redisTemplate.boundValueOps(key).get();
        if(!ObjectUtils.isEmpty(skuInfo)){
            //缓存有则直接返回
            return (SkuInfo) skuInfo;
        }
        //没有则查询数据库
        //加锁
        String lockKey=RedisConst.SKUKEY_PREFIX+skuId+RedisConst.SKULOCK_SUFFIX;
        String keyValue = UUID.randomUUID().toString().replace("-", "");
        Boolean isLock = redisTemplate.boundValueOps(lockKey).setIfAbsent(keyValue, RedisConst.SKULOCK_EXPIRE_PX2, TimeUnit.SECONDS);
        if(isLock){
            //加锁成功,从数据库查询
            SkuInfo skuInfoDb = skuInfoMapper.selectById(skuId);
            if(ObjectUtils.isEmpty(skuInfoDb)){
                //skuInfo为空，防止缓存穿透，短暂保存到redis中
                redisTemplate.boundValueOps(key).set(new SkuInfo(),RedisConst.SKUKEY_ISNULL_TIMEOUT,TimeUnit.SECONDS);
            }else {
                //skuInfo不为空，直接保存到redis
                redisTemplate.boundValueOps(key).set(skuInfoDb,RedisConst.SKUKEY_TIMEOUT,TimeUnit.SECONDS);
            }
            //释放锁
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setResultType(Long.class);
            script.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
            redisTemplate.execute(script, Arrays.asList(lockKey),keyValue);
            return skuInfoDb;
        }else {
            //加锁失败，睡眠之后重试
            try {
                Thread.sleep(RedisConst.SKUTRY_SLEEP);
                getSkuInfoLockManual(skuId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 查询缓存并使用redisson实现分布式锁
     * @param skuId
     * @return
     */
    public SkuInfo getSkuInfoLockAuto(Long skuId){
        //参数校验
        if(ObjectUtils.isEmpty(skuId)){
            return null;
        }
        //定义key
        String key = RedisConst.SKUKEY_PREFIX+ skuId +RedisConst.SKUKEY_SUFFIX;
        //从redis从获取
        Object o = redisTemplate.boundValueOps(key).get();
        if(!ObjectUtils.isEmpty(o)){
            //缓存不为空则直接返回
            return (SkuInfo)o;
        }
        //缓存为空则先加锁再查询数据库
        String lockKey = RedisConst.SKUKEY_PREFIX+ skuId + RedisConst.SKULOCK_SUFFIX;
        //获取锁
        RLock lock = redissonClient.getLock(lockKey);
        try {
            //尝试加锁
            lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1,RedisConst.SKULOCK_EXPIRE_PX2,TimeUnit.SECONDS);
            //查询数据库
            SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
            if(ObjectUtils.isEmpty(skuInfo)){
                //skuInfo为空，为防止缓存穿透，则短暂存储到redis
                redisTemplate.boundValueOps(key).set(skuInfo,RedisConst.SKUKEY_ISNULL_TIMEOUT,TimeUnit.SECONDS);
            }else {
                //skuInfo不为空,保存到redis
                redisTemplate.boundValueOps(key).set(skuInfo,RedisConst.SKUKEY_TIMEOUT,TimeUnit.SECONDS);
            }
            return skuInfo;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //释放锁
            if(lock.isLocked()&& lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
        return null;
    }

    /**
     * 获取全部分类信息
     *
     * @return
     */
    @Override
    public List<JSONObject> getBaseCategoryList() {
        List<JSONObject> list = new ArrayList<>();
        //查询分类信息
        List<BaseCategoryView> baseCategoryViews = baseCategoryViewMapper.selectList(null);
        //按一级分类的Id进行分组
        Map<Long, List<BaseCategoryView>> category1Map = baseCategoryViews.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory1Id));
        //解析分组后的信息
        for (Map.Entry<Long, List<BaseCategoryView>> category1 : category1Map.entrySet()) {
            //构建一个一级分类节点
            JSONObject category1Child = new JSONObject();
            //获取一级分类id
            Long category1Id =category1.getKey();
            //获取该一级分类id下所对应的分类信息
            List<BaseCategoryView> category2List = category1.getValue();
            //获取该一级分类的名字
            String category1Name=null;
            if(category2List.size()>0){
                 category1Name = category1.getValue().get(0).getCategory1Name();
            }
            //将该一级分类id下的信息再次用二级分类id进行分组
            Map<Long, List<BaseCategoryView>> category2Map = category2List.stream().collect(Collectors.groupingBy(BaseCategoryView::getCategory2Id));
            List<JSONObject> category2ChildList = new ArrayList<>();
            //解析category2Map
            for (Map.Entry<Long, List<BaseCategoryView>> category2 : category2Map.entrySet()) {
                JSONObject category2Child = new JSONObject();
                //获取二级分类id
                Long category2Id = category2.getKey();
                //获取该二级分类id下所对应的分类信息
                List<BaseCategoryView> category3List = category2.getValue();
                //获取二级分类的名字
                String category2Name=null;
                if(category3List.size()>0){
                    category2Name = category3List.get(0).getCategory2Name();
                }
                //将该二级分类id对应的所有三级分类信息构建为节点
                List<JSONObject> category3ChildList = category3List.stream().map(category3 -> {
                    JSONObject category3Child = new JSONObject();
                    category3Child.put("categoryId", category3.getCategory3Id());
                    category3Child.put("categoryName", category3.getCategory3Name());
                    return category3Child;
                }).collect(Collectors.toList());
                //完成二级分类节点
                category2Child.put("categoryId",category2Id);
                category2Child.put("categoryName",category2Name);
                category2Child.put("categoryChild",category3ChildList);
                //组装所有二级分类节点
                category2ChildList.add(category2Child);
            }
            //完成一级分类节点
            category1Child.put("categoryId",category1Id);
            category1Child.put("categoryName",category1Name);
            category1Child.put("categoryChild",category2ChildList);
            //组装所有一级分类节点
            list.add(category1Child);
        }
        return  list;
    }

    /**
     * 根据sku的品牌id查询品牌
     *
     * @param tmId
     * @return
     */
    @Override
    public BaseTrademark getTrademarkById(Long tmId) {
        return baseTrademarkMapper.selectById(tmId);
    }

    /**
     * 根据skuId查询商品对应的平台属性
     *
     * @param skuId
     * @return
     */
    @Override
    public List<BaseAttrInfo> getBaseAttrInfoListBySkuId(Long skuId) {
        return baseAttrInfoMapper.selectBaseAttrInfoListBySkuId(skuId);
    }

    /**
     * 扣库存
     *
     * @param skuStore
     */
    @Override
    public void decountStore(Map<String, String> skuStore) {
        for (Map.Entry<String, String> skuEntry : skuStore.entrySet()) {
            String skuId = skuEntry.getKey();
            String skuNum = skuEntry.getValue();
            int i = skuInfoMapper.updateStore(skuId, skuNum);
            if(i<=0){
                throw new RuntimeException(skuId+"商品扣减库存失败，数量为："+skuNum);
            }
        }
    }

    /**
     * 回滚库存
     *
     * @param skuStore
     */
    @Override
    public void rollbackStore(Map<String, String> skuStore) {
        for (Map.Entry<String, String> skuEntry : skuStore.entrySet()) {
            String skuId = skuEntry.getKey();
            String skuNum = skuEntry.getValue();
            int i = skuInfoMapper.rollbackStore(skuId, skuNum);
            if(i<=0){
                throw new RuntimeException(skuId+" 商品回滚库存失败，数量为："+skuNum);
            }
        }
    }
}
