package com.atguigu.gmall.activity.task;

import com.atguigu.gmall.activity.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.activity.util.DateUtil;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class GoodsFromDbToRedisTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    /**
     * 每20秒执行一次定时任务，将数据库指定时段的数据插入redis
     * @throws Exception
     */
    @Scheduled(cron = "0/20 * * * * *")
    public void goodsTask() throws Exception{
        //获取当前以及之后的四个时间段
        List<Date> dateMenus = DateUtil.getDateMenus();
        for (Date dateMenu : dateMenus) {
            //遍历时间段添加商品到redis
            //获取时间段的起始时间
            String startTime = DateUtil.data2str(dateMenu,DateUtil.PATTERN_YYYY_MM_DDHHMM);
            //获取时间段的截至时间
            String endTime = DateUtil.data2str(DateUtil.addDateHour(dateMenu,2),DateUtil.PATTERN_YYYY_MM_DDHHMM);
            //设置存储到redis的key
            String key = DateUtil.data2str(dateMenu,DateUtil.PATTERN_YYYYMMDDHH);
            //获取已经在redis中的商品id
            Set ids = redisTemplate.boundHashOps(key).keys();
            //构建查询条件查询改时段未存储再redis中的商品
            LambdaQueryWrapper<SeckillGoods> queryWrapper = new LambdaQueryWrapper<>();
            //查询改时间段内审核通过，库存大于0且不在redis中的秒杀商品
            queryWrapper.ge(SeckillGoods::getStartTime,startTime)
                        .le(SeckillGoods::getEndTime,endTime)
                        .eq(SeckillGoods::getStatus,"1")
                        .gt(SeckillGoods::getStockCount,0);
            if(!ObjectUtils.isEmpty(ids)){
                        queryWrapper.notIn(SeckillGoods::getId,ids);
            }
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectList(queryWrapper);
            //将查询到的秒杀商品添加到缓存中
            for (SeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps(key).put(seckillGood.getId()+"",seckillGood);
                //获取商品库存，构建元素队列
                Integer stockCount = seckillGood.getStockCount();
                String [] idArray =getIds(stockCount,seckillGood.getId());
                //使用该数组构建redis队列
                redisTemplate.boundListOps("secKillGoods_stock_queue"+seckillGood.getId()).leftPushAll(idArray);
                //构建用于展示的库存值缓存
                redisTemplate.boundHashOps("secKillGoods_stock_num").increment(seckillGood.getId()+"",stockCount);
            }


        }

    }

    /**
     * 根据库存构建数组
     * @param stockCount
     * @param id
     * @return
     */
    private String[] getIds(Integer stockCount, Long id) {
        String [] strArray = new String[stockCount];
        for (int i = 0; i < stockCount; i++) {
            strArray[i]=id+"";
        }
        return strArray;
    }

}
