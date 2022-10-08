package com.shz.controller;

import com.shz.dto.RespBean;
import com.shz.dto.UserDTO;
import com.shz.service.FollowService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
@RestController
public class FollowController {
    @Resource
    private FollowService followService;

    @PutMapping("/{id}/{isFollow}")
    public RespBean follow(@PathVariable("id") Integer followUserId, @PathVariable("isFollow") Boolean isFollow) {
        return followService.follow(followUserId, isFollow);
    }

    @GetMapping("/or/not/{fuid}")
    public boolean isFollow(@PathVariable("fuid") Integer followUserId) {
        return followService.isFollow(followUserId);
    }

    @GetMapping("/common/{fuid}")
    public List<UserDTO> followCommons(@PathVariable("fuid") Integer followUserId){
        return followService.followCommons(followUserId);
    }
    @GetMapping("/follow/{uid}")
    public List<UserDTO> getThisUserFollow(@PathVariable("uid") Integer uid){
        return followService.getThisUserFollows(uid);
    }
}
