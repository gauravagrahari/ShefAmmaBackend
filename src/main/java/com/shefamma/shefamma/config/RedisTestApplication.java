package com.shefamma.shefamma.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

@SpringBootApplication(scanBasePackages = "com.shefamma.shefamma")
public class RedisTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisTestApplication.class, args);
    }

    @Bean
    public ApplicationListener<ContextRefreshedEvent> onStartUp(RedisTestService redisTestService) {
        return event -> redisTestService.testRedis();
    }

}
