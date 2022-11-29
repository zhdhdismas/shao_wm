package com.shz.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient(){
        // 配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.1.102:6379").setPassword("200273shz");
//        config.useSingleServer().setAddress("redis://192.168.1.104:6379").setPassword("200273shz");
        // 创建RedissonClient对象
        return Redisson.create(config);
    }
}
