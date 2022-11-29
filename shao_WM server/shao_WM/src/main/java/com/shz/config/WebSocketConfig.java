package com.shz.config;

import cn.hutool.core.util.StrUtil;
import com.shz.dto.JwtProperties;
import com.shz.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.annotation.Resource;

/**
 * WebSocket配置类
 *
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private UserDetailsService userDetailsService;
    @Resource
    private JwtProperties jwtProperties;

    /**
     * 添加这个Endpoint，这样在网页可以通过websocket连接上服务
     * 也就是我们配置websocket的服务地址，并且可以指定是否使用socketJS
     * @param registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /**
         * 1.将ws/ep路径注册为stomp的端点，用户连接了这个端点就可以进行websocket通讯，支持
         socketJS
         * 2.setAllowedOrigins("*")：允许跨域
         * 3.withSockJS():支持socketJS访问
         */
//        registry.addEndpoint("/ws/ep").setAllowedOrigins("*").withSockJS();
        registry.addEndpoint("/ws/ep").setAllowedOriginPatterns("*").withSockJS();
    }

    /**
     * 输入通道参数配置
     * @param registration
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                //判断是否为连接，如果是，需要获取token，并且设置用户对象
                if (StompCommand.CONNECT.equals(accessor.getCommand())){
                    String token = accessor.getFirstNativeHeader("Auth-Token");
                    if (!StrUtil.isEmpty(token)){
                        String authToken = token.substring(7);
                        if(authToken.length()>8){
                            String username = jwtTokenUtil.getUsernameFromToken(authToken);
                            if(username!=null){
                                //token中存在用户名
                                //登录
                                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                                //验证token是都有效
                                if (userDetails!=null&&jwtTokenUtil.validateToken(authToken,userDetails)){
                                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                                    accessor.setUser(authenticationToken);
                                }
                            }
                        }


                    }
                }
                return message;
            }
        });
    }

    /**
     * 配置消息代理
     * @param registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //配置代理域，可以配置多个，配置代理目的地前缀为/queue,可以在配置域上向客户端推送消息
        registry.enableSimpleBroker("/queue");
    }

}