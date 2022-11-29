package com.shz;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@SpringBootTest(classes = MailServerApplication.class)
// 让 JUnit 运行 Spring 的测试环境， 获得 Spring 环境的上下文的支持
@RunWith(SpringRunner.class)
class MailServerApplicationTests {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Test
    void contextLoads() {
        List<String> s= stringRedisTemplate.opsForList().range("address:38", 0, -1);
        for (String s1 : s) {
            System.out.println(s1);
        }
    }

}
