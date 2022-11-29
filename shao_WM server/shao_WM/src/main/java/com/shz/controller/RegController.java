package com.shz.controller;

import com.shz.dto.RespBean;
import com.shz.entity.User;
import com.shz.service.UserService;
import com.shz.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class RegController {
    @Resource
    UserService userService;

    @RequestMapping(value = "/reg", method = RequestMethod.POST)
    public RespBean reg(User user) {
        int result = userService.reg(user);
        if (result==0) {
            return new RespBean("error", "用户名重复，注册失败!");
        } else if(result==1) {
            return new RespBean("error","邮箱已被绑定,注册失败");
        }else{
            return new RespBean("success", "注册成功!");
        }
    }
}
