package com.shz.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * RabbitMQ配置
 *
 * @author Tang
 * @version 1.0
 * @date 2022/05/02 23:23:27
 */
@Configuration
public class RabbitMQConfig {
    @Resource ObjectMapper objectMapper;

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
