package com.shz.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.shz.ShaoWmApplication;
import com.shz.dto.UserDTO;
import com.shz.entity.User;
import com.shz.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.shz.utils.RedisConstants.CACHE_USER_KEY;
import static com.shz.utils.RedisConstants.CACHE_USER_TTL;

public class UserUtil {

    public static User getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext ().getAuthentication ().getPrincipal ();
        return user;
    }
    public static List<UserDTO> toUserDTOs(List<User> Users){
        List<UserDTO> res=new ArrayList<>();
        for (User user : Users) {
            UserDTO userDTO=new UserDTO();
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setPhone(user.getPhone());
            userDTO.setUid(user.getUid());
            userDTO.setEnabled(user.isEnabled());
            userDTO.setStaff(user.isStaff());
            userDTO.setRegTime(user.getRegTime());
            userDTO.setAvatar("http://"+OSSUtil.SUFFER_URL+user.getAvatar());
            res.add(userDTO);
        }
        return res;
    }
    public static UserDTO toUserDTO(User user){
        UserDTO userDTO=new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setUid(user.getUid());
        userDTO.setEnabled(user.isEnabled());
        userDTO.setStaff(user.isStaff());
        userDTO.setRegTime(user.getRegTime());
        userDTO.setAvatar("http://"+OSSUtil.SUFFER_URL+user.getAvatar());
        return userDTO;
    }

}
