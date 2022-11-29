package com.shz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.dto.RespBean;
import com.shz.dto.UserDTO;
import com.shz.entity.Address;
import com.shz.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService extends IService<User> {
    List<Address> getCurrentUserAddress();

    User getResultUser(List<Address> address);

    boolean compareTwoPassword(String password);

    boolean updateUserMsg(User user, String curpassword);

    UserDetails loadUserByUsername(String username);

    Integer reg(User user);

    boolean compareUserName(String username);


    String addAvatar(String avatar);

    RespBean sign();

    Integer signCount();

    List<String> getSignList();

    User getAUserOrUserCache(Integer uid);

    boolean compareUserEmail(String email);

    UserDTO getCurrentUserBasic();
}
