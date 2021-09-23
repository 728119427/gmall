package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.RedisTestService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RedisTestServiceImpl implements RedisTestService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * redis的锁测试
     */

/*    @Override
    public synchronized void lockDemo() {
        BoundValueOperations<String, String> valueOps = stringRedisTemplate.boundValueOps("num");
        String num = valueOps.get();
        if(StringUtils.isEmpty(num)){
            return;
        }
        int numValue = Integer.parseInt(num);
        valueOps.set(++numValue+"");

    }*/

    /**
     * 手动实现redis分布式锁
     */
/*    @Override
    public void lockDemo() {
        //设置uuid只有自己能删除锁
        String keyValue = UUID.randomUUID().toString().replace("-", "");
        //加锁,有效时间为两秒，两秒后自动释放锁，避免死锁
        BoundValueOperations<String, String> lockOps = stringRedisTemplate.boundValueOps("lock");
        Boolean aBoolean = lockOps.setIfAbsent(keyValue, 2, TimeUnit.SECONDS);
        if(aBoolean){
            //获取到锁
            //进行操作,取值加一
            BoundValueOperations<String, String> numOps = stringRedisTemplate.boundValueOps("num");
            String num = numOps.get();
            if(num==null){
                return;
            }
            int intNum = Integer.parseInt(num);
            numOps.set(++intNum+"");
            //操作完成手动释放锁
        *//*    if(lockOps.get().equals(keyValue)){
                stringRedisTemplate.delete("lock");
            }*//*
            //使用lua脚本释放锁，使释放锁为原子性操作，避免线程切换导致误删，进入if判断之后线程切换可能会导致误删
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
            script.setResultType(Long.class);
            stringRedisTemplate.execute(script, Arrays.asList("lock"),keyValue);
        }else {
            //没有获取到锁，睡眠1秒后重试
            try {
                Thread.sleep(1000);
                lockDemo();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }*/

    /**
     * 使用redisson实现分布式锁
     */
    @Override
    public void lockDemo() {
        //获取锁
        RLock lock = redissonClient.getLock("lock");
        try {
            //重试10秒，锁的有效期为1秒
            boolean isLock = lock.tryLock(10, 1, TimeUnit.SECONDS);
            if(isLock){
                //加锁成功
                String num = stringRedisTemplate.opsForValue().get("num");
                if(StringUtils.isEmpty(num)){
                    return;
                }
                int intNum = Integer.parseInt(num);
                //num不为空则加一
                stringRedisTemplate.opsForValue().set("num",++intNum+"");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //释放锁
            lock.unlock();
        }

    }
}
