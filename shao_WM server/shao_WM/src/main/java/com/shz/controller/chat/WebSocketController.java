package com.shz.controller.chat;

import com.shz.entity.ChatMsg;
import com.shz.entity.User;
import com.shz.utils.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Controller
public class WebSocketController {
    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/ws/chat")
    public void handleMsg(Authentication authentication, ChatMsg chatMsg){
        User u = (User) authentication.getPrincipal();
        chatMsg.setFrom(u.getUsername());
        chatMsg.setFromNickName(u.getUsername());
        chatMsg.setDate(LocalDateTime.now());
        /**
         * 发送消息
         * 1.消息接收者
         * 2.消息队列
         * 3.消息对象
         */
        simpMessagingTemplate.convertAndSendToUser(chatMsg.getTo(),"/queue/chat",chatMsg);
    }
}