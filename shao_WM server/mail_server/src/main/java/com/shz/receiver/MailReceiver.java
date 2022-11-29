package com.shz.receiver;


import cn.hutool.json.JSONUtil;
import com.rabbitmq.client.Channel;
import com.shz.config.MailConstants;
import com.shz.pojo.Staff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Date;


@Component
public class MailReceiver {
    public static final Logger logger = LoggerFactory.getLogger(MailReceiver.class);

    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private MailProperties mailProperties;
    @Resource
    private TemplateEngine templateEngine;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 邮件发送
     */
    @RabbitListener(queues = MailConstants.MAIL_QUEUE_NAME)
    public void handler(Message message, Channel channel) {
        System.out.println(message.getPayload());
        Staff staff = JSONUtil.toBean((String) message.getPayload(), Staff.class);
        System.out.println("MailReceiver:  staff = " + staff);
        MessageHeaders headers = message.getHeaders();
        long tag = (long) headers.get(AmqpHeaders.DELIVERY_TAG);
        System.out.println("tag = " + tag);
        String msgId = (String) headers.get("spring_returned_message_correlation");
        System.out.println("msgId = " + msgId);
        HashOperations hash = stringRedisTemplate.opsForHash();

        try {
            if (hash.entries("mail_log").containsKey(msgId)) {
                //redis中包含key，说明消息已经被消费
                logger.info("消息已经被消费========>{}", msgId);
                /**
                 * 手动确认消息
                 * tag:消息序号
                 * multiple:是否多条
                 */
                channel.basicAck(tag, false);
                return;
            }
            MimeMessage msg = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg);
            helper.setFrom(mailProperties.getUsername());
            helper.setTo(staff.getEmail());
            helper.setSubject("入职邮件");
            helper.setSentDate(new Date());
            Context context = new Context();
            context.setVariable("name", staff.getUsername());
            context.setVariable("posName", staff.getName());
            context.setVariable("joblevelName", staff.getLevelName());
            context.setVariable("departmentName", staff.getDepartment());
            String mail = templateEngine.process("mail", context);
            helper.setText(mail, true);
            //发送邮件
            javaMailSender.send(msg);
            logger.info("邮件发送成功");
            //将消息id存入redis
            hash.put("mail_log", msgId, "OK");
            System.out.println("MailReceiver: redis---> msgId = " + msgId);
            //手动确认消息
            channel.basicAck(tag, false);
        } catch (Exception e) {
            try {
                channel.basicNack(tag, false, true);
            } catch (IOException ioException) {
                //ioException.printStackTrace();
                logger.error("消息确认失败=====>{}", ioException.getMessage());
            }
            logger.error("MailReceiver + 邮件发送失败========{}", e.getMessage());
        }
    }

//    /**
//     * 邮件发送
//     */
//    @RabbitListener(queues = MailConstants.MAIL_QUEUE_NAME)
//    public void handler(String json) {
//        System.out.println(json);
//        Staff staff = JSONUtil.toBean(json, Staff.class);
//        System.out.println(staff.getAddress()+":"+staff.getUsername());
//        MimeMessage msg=javaMailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(msg);
//        try {
//            helper.setFrom(mailProperties.getUsername());
//            helper.setTo(staff.getEmail());
//            helper.setSubject("入职邮件");
//            helper.setSentDate(new Date());
//            Context context = new Context();
//            context.setVariable("name", staff.getUsername());
//            context.setVariable("posName", staff.getName());
//            context.setVariable("joblevelName", staff.getLevelName());
//            context.setVariable("departmentName", staff.getDepartment());
//            String mail = templateEngine.process("mail", context);
//            helper.setText(mail, true);
//            //发送邮件
//            javaMailSender.send(msg);
//            logger.info("邮件发送成功");
//
//        } catch (Exception e) {
//            logger.error("MailReceiver + 邮件发送失败========{}", e.getMessage());
//        }
//    }


}
