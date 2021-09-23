package com.atguigu.gmall.common.cache;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.constant.RedisConst;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class MyGmallCacheAspect {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Around("@annotation(com.atguigu.gmall.common.cache.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint joinPoint){
        Object result =null;
        //获取方法参数
        Object[] args = joinPoint.getArgs();
        //获取方法上注解得值以便定义key从redis中查找
        MethodSignature methodSignature=(MethodSignature)joinPoint.getSignature();
        GmallCache gmallCache = methodSignature.getMethod().getAnnotation(GmallCache.class);
        String prefix = gmallCache.prefix();
        //定义key值
        String key = prefix + Arrays.toString(args) +RedisConst.SKUKEY_SUFFIX;
        //从redis中查找缓存
        result = cacheHit(methodSignature,key);
        if(!ObjectUtils.isEmpty(result)){
            return result;
        }
        //缓存为空则加锁后从数据库中查找
        //获取锁
        String lockKey = prefix + Arrays.toString(args) + RedisConst.SKULOCK_SUFFIX;
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1,RedisConst.SKULOCK_EXPIRE_PX2,TimeUnit.SECONDS);
            //进行数据库查询
            result=joinPoint.proceed(args);
            if(ObjectUtils.isEmpty(result)){
                //数据库查询值为空，为防止缓存穿透添加一个空值但不是null，短暂添加到redis
                Object o = new Object();
                //将对象转换为json保存到redis中，方便取出缓存时进行数据类型转换，直接存储对象，易发生类型转换或序列化问题
                redisTemplate.boundValueOps(key).set(JSONObject.toJSONString(o),RedisConst.SKUKEY_ISNULL_TIMEOUT, TimeUnit.SECONDS);
                return null;
            }
                //不为空
                redisTemplate.boundValueOps(key).set(JSONObject.toJSONString(result),RedisConst.SKUKEY_TIMEOUT,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }finally {
            //释放锁
            lock.unlock();
        }

        return result;
    }

    /**
     * 查询缓存
     * @param methodSignature
     * @param key
     * @return
     */
    private Object cacheHit(MethodSignature methodSignature, String key) {
        String value=(String)redisTemplate.opsForValue().get(key);
        if(null==value){
            return null;
        }
        Class<?> returnType = methodSignature.getMethod().getReturnType();
        return JSONObject.parseObject(value, returnType);
    }
}
