package com.shefamma.shefamma.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisTestService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void testRedis() {
        redisTemplate.opsForValue().set("testKey", "Hello Redis!");
        System.out.println("Value from Redis: " + redisTemplate.opsForValue().get("testKey"));
    }
}
