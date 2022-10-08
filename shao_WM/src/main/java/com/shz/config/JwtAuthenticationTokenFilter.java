package com.shz.config;

import com.shz.dto.JwtProperties;
import com.shz.utils.JwtTokenUtil;
import com.shz.utils.UserUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Resource
    private JwtProperties jwtProperties;



    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String requestUrl = httpServletRequest.getRequestURI();

        String authToken = httpServletRequest.getHeader(jwtProperties.getHeader());
        if(authToken==null||authToken.length()<=7){
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        String token=authToken.substring(7);
        String stuId = jwtTokenUtil.getUsernameFromToken(token);
//        当token中的username不为空时进行验证token是否是有效的token     token中是否能解析出stuId
        if(SecurityContextHolder.getContext().getAuthentication() != null){
            if(!Objects.equals(stuId, UserUtil.getCurrentUser().getUsername())){
                SecurityContextHolder.getContext().setAuthentication(null);
            }
        }
        if (stuId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(stuId);
            System.out.println("#"+stuId);
            if (userDetails!=null&&jwtTokenUtil.validateToken(token, userDetails)) { //如username不为空，并且能够在数据库中查到

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

                //将authentication放入SecurityContextHolder中
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

}

