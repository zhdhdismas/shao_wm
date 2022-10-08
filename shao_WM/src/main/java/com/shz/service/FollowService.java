package com.shz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.dto.RespBean;
import com.shz.dto.UserDTO;
import com.shz.entity.Follow;

import java.util.List;

public interface FollowService extends IService<Follow> {
    RespBean follow(Integer followUserId, Boolean isFollow);

    boolean isFollow(Integer followUserId);

    List<UserDTO> followCommons(Integer followUserId);

    List<UserDTO> getThisUserFollows(Integer uid);
}
