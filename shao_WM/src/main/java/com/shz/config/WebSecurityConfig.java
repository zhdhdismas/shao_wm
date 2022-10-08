package com.shz.config;

import com.alibaba.fastjson.JSON;
import com.shz.entity.User;
import com.shz.service.impl.UserServiceImpl;
import com.shz.utils.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created by sang on 2017/12/17.
 */
@Configuration

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Resource
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    UserServiceImpl userService;
    @Resource
    private DataSource dataSource;


    //     自定义的Jwt Token过滤器
    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/admin/category/all").permitAll()
                .antMatchers("/admin/**", "/manage/**").hasRole("2")
                .antMatchers("/rider/**").hasRole("1")

                .anyRequest().authenticated()

                .and().formLogin().loginPage("/").loginProcessingUrl("/login")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
//                         httpServletResponse.setContentType ( "application/json;charset=utf-8" );
//                         httpServletResponse.setHeader ( "Access-Control-Allow-Origin", "*" );
                        final String realToken = jwtTokenUtil.generateToken(authentication.getName());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("token", realToken);
                        map.put("status", "success");

                        //将生成的authentication放入容器中，生成安全的上下文
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        String json = JSON.toJSONString(map);
                        httpServletResponse.setContentType("text/json;charset=utf-8");
                        httpServletResponse.getWriter().write(json);

//                        PrintWriter out = httpServletResponse.getWriter();
//                        out.write("{\"status\":\"success\",\"msg\":\"登录成功\"}");
//                        out.flush();
//                        out.close();

                    }


                }).failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        PrintWriter out = httpServletResponse.getWriter();
                        out.write("{\"status\":\"error\",\"msg\":\"登录失败\"}");
                        out.flush();
                        out.close();

                    }
                }).and().exceptionHandling().accessDeniedHandler(getAccessDeniedHandler())
                .and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and().logout().logoutSuccessUrl("/")

                .and().rememberMe().tokenValiditySeconds(7*24*60*60).userDetailsService(userService)
                .rememberMeServices(getRememberMeServices())
                .key("INTERNAL_SECRET_KEY")
                .and().csrf().disable().sessionManagement().maximumSessions(1)
                .expiredSessionStrategy(new CustomExpiredSessionStrategy());

//                配置自己的jwt验证过滤器
        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);


    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/reg", "/upload", "/blogimg/**",
                "/index.html", "/static/**","/email/**","/alipay/**");
    }

    @Bean
    AccessDeniedHandler getAccessDeniedHandler() {
        return new AuthenticationAccessDeniedHandler();
    }

    @Bean
    public RememberMeServices getRememberMeServices() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
//        自动建表,第二次启动时要注释掉
//        jdbcTokenRepository.setCreateTableOnStartup(true);
        PersistentTokenBasedRememberMeServices rememberMeServices
                = new PersistentTokenBasedRememberMeServices("INTERNAL_SECRET_KEY", userService, jdbcTokenRepository);
        rememberMeServices.setParameter("rememberMe");
        return rememberMeServices;
    }
}