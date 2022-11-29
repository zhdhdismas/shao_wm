package com.shz.controller.chat;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shz.dto.RespBean;
import com.shz.dto.UserDTO;
import com.shz.entity.ChatMsg;
import com.shz.entity.User;
import com.shz.service.UserService;
import com.shz.utils.UserUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    UserService userService;
    @GetMapping("/all")
    public List<UserDTO> getAllUsers(String username){
        Page<User> pages = userService.query().like("username", username).page(new Page<>(1, 5));
        return UserUtil.toUserDTOs(pages.getRecords());
    }
    @PostMapping("/sessions")
    public synchronized RespBean postSessions(@RequestBody HashMap<String,Object> map){
        stringRedisTemplate.opsForValue().set("chat-sessions:", JSONUtil.toJsonStr(map));
        return new RespBean("success","发送成功");
    }

    @GetMapping("/getSessions")
    public Object getSessions(){
        Boolean b = stringRedisTemplate.hasKey("chat-sessions:");
        if(b==null|| !b){
            return 1;
        }
        return stringRedisTemplate.opsForValue().get("chat-sessions:");
    }

}
