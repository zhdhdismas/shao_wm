package com.shz.controller;

import cn.hutool.core.util.ObjectUtil;
import com.shz.dto.Email;
import com.shz.dto.RespBean;
import com.shz.entity.User;
import com.shz.service.UserService;
import com.shz.utils.JwtTokenUtil;
import com.shz.utils.MailUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/email")
public class MailController {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    UserService userService;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private PasswordEncoder passwordEncoder;


    @PostMapping("/send")
    public RespBean emailSend(Email email) {
        if(email.getEmail()==null||email.getEmail().length()==0){
            return new RespBean("error","信件格式有误");
        }

        Random r = new Random();
        Integer sum = 0;
        for (int i = 0; i < 6; i++) {
            int x = r.nextInt(10);
            sum = 10 * sum + x;
        }
        MailUtils.sendMail(email.getEmail(), "你好,您的验证码为" + sum.toString(), "shao_餐馆验证码信件");
        stringRedisTemplate.opsForValue().set(email.getEmail(), sum.toString());
        return new RespBean("success", "发送成功");
    }

    @PostMapping("/login")
    public RespBean emailLogin(Email email)   {
        String s = stringRedisTemplate.opsForValue().get(email.getEmail());
        if(!ObjectUtil.equals(email.getCode(),s)){
            return new RespBean("error","验证码错误或超时");
        }
        User user= userService.query().eq("email", email.getEmail()).one();
        String name="";
        if(user==null){
            User u=new User();
            name = getUserName();
            u.setUsername(name);
            u.setEmail(email.getEmail());
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            u.setPassword(passwordEncoder.encode(uuid));
            u.setEnabled(true);
            u.setStaff(false);
            u.setRegTime(new Timestamp(System.currentTimeMillis()));
            userService.save(u);
        }else{
            if(!user.isEnabled()){
                return new RespBean("error","验证码错误或超时");
            }
            name=user.getUsername();
        }
        final String realToken = jwtTokenUtil.generateToken(name);
        return new RespBean("success",realToken);
    }

    public String getUserName() {
        LocalDateTime now=LocalDateTime.now();
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        long count = stringRedisTemplate.opsForValue().increment("icr:" + "user" + ":" + date);
        String res=date.substring(0, 4)+date.substring(5,7)+date.substring(8);
        return "user_"+res+count;
    }
}
