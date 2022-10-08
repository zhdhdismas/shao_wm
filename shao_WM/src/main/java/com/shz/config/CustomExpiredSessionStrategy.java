package com.shz.config;

import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import java.io.IOException;


public class CustomExpiredSessionStrategy implements SessionInformationExpiredStrategy {
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException,ServletException {
        event.getResponse().setContentType("application/json;charset=utf-8");
        event.getResponse().getWriter().write("{\"status\":\"over\",\"msg\":\"您的账号已在别地登录\"}");
    }
}
