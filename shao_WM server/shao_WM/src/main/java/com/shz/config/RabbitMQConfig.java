package com.shz.config;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.shz.entity.MailLog;
import com.shz.service.MailLogService;
import com.shz.utils.MailConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * RabbitMQ配置类
 */

@Configuration
public class RabbitMQConfig  {
    public static final Logger logger = LoggerFactory.getLogger(RabbitMQConfig.class);
    @Resource
    private CachingConnectionFactory cachingConnectionFactory;
    @Resource
    private MailLogService mailLogService;

    /**
     * 自动注入RabbitTemplate模板
     */
//    @Resource
//    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息JSON序列化
     */
//    @Override
//    public void afterPropertiesSet() {
//        //使用JSON序列化
//        RabbitTemplate rabbitTemplate = new RabbitTemplate();
//        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
//    }

    @Bean
    public RabbitTemplate rabbitTemplate(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        /**
         * 消息确认回调,确认消息是否到达broker
         * data:消息唯一标识
         * ack：确认结果
         * cause：失败原因
         */
        rabbitTemplate.setConfirmCallback((data,ack,cause)->{
            System.out.println(data);
            if(data!=null){
                String msgId = data.getId();
                System.out.println("RabbitMQConfig: msgId = " + msgId);
                if (ack){
                    logger.info("{}==========>消息发送成功",msgId);
                    mailLogService.update(new UpdateWrapper<MailLog>().set("status",1).eq("msgId",msgId));
                }else {
                    logger.info("{}==========>消息发送失败",msgId);
                }
            }

        });
        /**
         * 消息失败回调，比如router不到queue时回调
         * msg:消息主题
         * repCode:响应码
         * repText:响应描述* exchange:交换机
         * * routingKey:路由键
         * */
        rabbitTemplate.setReturnCallback((msg,repCode,repText,exchange,routingKey)->{
            logger.info("{}=======================>消息发送到queue时失败",msg.getBody());
        });
        return rabbitTemplate;
    }

    @Bean
    public Queue queue() {
        return new Queue(MailConstants.MAIL_QUEUE_NAME, true);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(MailConstants.MAIL_EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder.bind(queue()).to(directExchange()).with(MailConstants.MAIL_ROUTING_KEY_NAME);
    }
}