package com.shz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.dto.RespBean;
import com.shz.dto.UserDTO;
import com.shz.entity.Follow;
import com.shz.entity.User;
import com.shz.mapper.FollowMapper;
import com.shz.service.FollowService;
import com.shz.service.UserService;
import com.shz.utils.UserUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    UserService userService;
    @Override
    public RespBean follow(Integer followUserId, Boolean isFollow) {
        int uid = UserUtil.getCurrentUser().getUid();
        String key="follows:"+uid;
        persistenceFollows(uid,key);
        if(!isFollow){
            Follow follow=new Follow();
            follow.setUid(uid);
            follow.setFuid(followUserId);
            boolean isSuccess = save(follow);
            if(isSuccess){
                stringRedisTemplate.opsForSet().add(key,followUserId.toString());
//                stringRedisTemplate.expire(key,20, TimeUnit.HOURS);
                return new RespBean("success","关注成功");
            }
        }else{
            boolean isSuccess = remove(new QueryWrapper<Follow>().eq("uid", uid).eq("fuid", followUserId));
            if(isSuccess){
                stringRedisTemplate.opsForSet().remove(key,followUserId.toString());
                return new RespBean("info","已取消关注");
            }
        }
        return new RespBean("error","未知错误");

    }

    @Override
    public boolean isFollow(Integer followUserId) {
        int uid = UserUtil.getCurrentUser().getUid();
        String key="follows:"+uid;
        Boolean isMember = stringRedisTemplate.opsForSet().isMember(key, followUserId.toString());
        if(Boolean.TRUE.equals(isMember)){
            return true;
        }else{
            Integer count = query().eq("uid", uid).eq("fuid", followUserId).count();
            return count > 0;
        }
    }

    @Override
    public List<UserDTO> followCommons(Integer followUserId) {
        int uid = UserUtil.getCurrentUser().getUid();
        String key="follows:"+uid;
        String key2="follows:"+followUserId;
        persistenceFollows(uid,key);
        persistenceFollows(followUserId,key2);

        Set<String> intersect = stringRedisTemplate.opsForSet().intersect(key, key2);
        if(intersect==null||intersect.isEmpty()){
            return Collections.emptyList();
        }
        List<Integer> ids = intersect.stream().map(Integer::valueOf).collect(Collectors.toList());
        List<User> users = userService.query().in("uid", ids).list();
        return UserUtil.toUserDTOs(users);
    }

    @Override
    public List<UserDTO> getThisUserFollows(Integer uid) {
        String key="follows:"+uid;
        Set<String> members = stringRedisTemplate.opsForSet().members(key);
        if(members==null||members.size()==0){
            List<Follow> follows = query().eq("uid", uid).list();
            List<User> res=new ArrayList<>();
            for (Follow follow : follows) {
                int fuid = follow.getFuid();
                stringRedisTemplate.opsForSet().add(key,String.valueOf(fuid));
                User user = userService.getAUserOrUserCache(fuid);
                res.add(user);
            }
            return UserUtil.toUserDTOs(res);
        }
        List<Integer> ids = members.stream().map(Integer::valueOf).collect(Collectors.toList());
        List<User> res=new ArrayList<>();
        for (Integer id : ids) {
            User user = userService.getAUserOrUserCache(id);
            res.add(user);
        }
        return UserUtil.toUserDTOs(res);
    }
    public void persistenceFollows(int uid,String key){
        Boolean members = stringRedisTemplate.hasKey(key);
        if(Boolean.FALSE.equals(members)){
            List<Follow> follows = query().eq("uid", uid).list();
            for (Follow follow : follows) {
                int fuid = follow.getFuid();
                stringRedisTemplate.opsForSet().add(key,String.valueOf(fuid));
            }
        }
    }


}
