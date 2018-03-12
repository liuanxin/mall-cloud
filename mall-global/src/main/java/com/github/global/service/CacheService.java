package com.github.global.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnBean({ RedisTemplate.class, StringRedisTemplate.class })
public class CacheService {

    /** @see org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /** 往 redis 中放值: set key value */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }
    /** 往 redis 放值, 并设定超时时间: setex key seconds value */
    public void set(String key, String value, long timeout, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }
    /** 往 redis 放值, 调用成功者返回 true. 此方法如果被多线程调用, 只有一个会返回 true, 其他会返回 false: setnx key 1 */
    public boolean setIfAbsent(String key) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, "1");
    }
    /** 往 redis 放值并设置过期时间, 调用成功者返回 true. 此方法如果被多线程调用, 只有一个会返回 true, 其他会返回 false: setnx key 1 */
    public boolean setIfAbsent(String key, long timeout, TimeUnit timeUnit) {
        // redisTemplate 不支持 set key value EX timeout NX 这种命令
        boolean flag = setIfAbsent(key);
        boolean expireFlag = stringRedisTemplate.expire(key, timeout, timeUnit);
        return flag && expireFlag;
    }
    /** 从 redis 中取字符: get key */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
    /** 从 redis 中删值: del key */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }


    /** 向队列(先进先出)写值(从左边压栈): lpush key */
    public void listPush(String key, String value) {
        stringRedisTemplate.opsForList().leftPush(key, value);
    }
    /** 向队列(先进先出)读值(从右边出栈): rpop key */
    public Object listPop(String key) {
        return stringRedisTemplate.opsForList().rightPop(key);
    }


    /** 获取指定 set 的长度: scard key */
    public long setSize(String key) {
        return stringRedisTemplate.opsForSet().size(key);
    }
    /** 将指定的 set 存进 redis 的 set 并返回成功条数: sadd key v1 v2 v3 ... */
    public long setAdd(String key, String[] set) {
        return stringRedisTemplate.opsForSet().add(key, set);
    }
    /** 从指定的 set 中随机取一个值: spop key */
    public Object setPop(String key) {
        return stringRedisTemplate.opsForSet().pop(key);
    }
}
