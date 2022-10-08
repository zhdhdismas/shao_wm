package com.shz.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.config.MyPasswordEncoder;
import com.shz.dto.RespBean;
import com.shz.entity.Address;
import com.shz.entity.User;
import com.shz.mapper.UserMapper;
import com.shz.service.AddressService;
import com.shz.service.UserService;
import com.shz.utils.DateUtil;
import com.shz.utils.OSSUtil;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.shz.utils.UserUtil;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.shz.utils.RedisConstants.*;
import static com.shz.utils.SystemConstants.YEAR_AND_MONTHS;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService, UserDetailsService {


    @Resource
    MyPasswordEncoder passwordEncoder;

    @Resource
    AddressService addressService;

    @Resource
    StringRedisTemplate stringRedisTemplate;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = query().eq("username", username).one();
        if (user == null) {
            //避免返回null，这里返回一个不含有任何值的User对象，在后期的密码比对过程中一样会验证失败
            return null;
        }
        //查询用户的角色信息，并返回存入user中
        return user;
    }

    @Override
    public Integer reg(User user) {
        UserDetails userDetails = loadUserByUsername(user.getUsername());
        if (userDetails != null) {
            return 0;
        }
        Integer count = query().eq("email", user.getEmail()).count();
        if(count>0){
            return 1;
        }
        //插入用户,插入之前先对密码进行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);//用户可用
        user.setRegTime(new Timestamp(System.currentTimeMillis()));
        save(user);
        return 2;
    }

    @Override
    public boolean compareUserName(String username) {
        return !UserUtil.getCurrentUser().getUsername().equals(username);
    }
    @Override
    public boolean compareUserEmail(String email) {
        return !UserUtil.getCurrentUser().getEmail().equals(email);
    }

    @Override
    public String addAvatar(String avatar) {
        int uid = UserUtil.getCurrentUser().getUid();
        int roleName = UserUtil.getCurrentUser().getRoleName();
        update(new UpdateWrapper<User>().set("avatar", "/" + avatar).eq("uid", uid));
        String scaleAfter = "http://" + OSSUtil.SUFFER_URL + "/" + avatar;
        return scaleAfter;

    }

    @Override
    public RespBean sign() {
        // 1.获取当前登录用户
        Integer userId = UserUtil.getCurrentUser().getUid();
        // 2.获取日期
        LocalDateTime now = LocalDateTime.now();
        // 3.拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + userId + keySuffix;
        // 4.获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();
        // 5.写入Redis SETBIT key offset 1
        Boolean isSign = stringRedisTemplate.opsForValue().getBit(key, dayOfMonth - 1);
        if (Boolean.TRUE.equals(isSign)) {
            return new RespBean("error", "已经签到了！！！");
        }
        stringRedisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);
        return new RespBean("success", "签到成功");
    }

    @Override
    public Integer signCount() {
        Integer userId = UserUtil.getCurrentUser().getUid();
        LocalDateTime now = LocalDateTime.now();
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + userId + keySuffix;
        int dayOfMonth = now.getDayOfMonth();

        List<Long> res = stringRedisTemplate.opsForValue().bitField(
                key,
                BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0)
        );
        if (res == null || res.isEmpty()) {
            return 0;
        }
        Long num = res.get(0);
        if (num == null || num == 0) {
            return 0;
        }
        int count = 0;
        while ((num&1)!=0) {
            count++;
            num >>= 1;
        }
        return count;
    }
    public List<String> getSignList(){
        List<String> res=new ArrayList<>();
        for (String yearAndMonth : YEAR_AND_MONTHS) {
            List<String> sign = getSign(yearAndMonth);
            res.addAll(sign);
        }
        return res;
    }
    public List<String> getSign(String yearAndMonth) {
        String year = yearAndMonth.substring(0, 4);
        String month = yearAndMonth.substring(4);
        String ss = ":";
        ss += year;
        String formatMonth = "";
        if (month.length() == 1) {
            ss += "0";
            formatMonth += "0";
        }
        formatMonth += month;
        ss += month;
        int userId = UserUtil.getCurrentUser().getUid();

        String key = USER_SIGN_KEY + userId + ss;
        //        1 01000 00000 00000 0000 000
        int days= DateUtil.dayOfMonth(year,month);
        List<Long> list = stringRedisTemplate.opsForValue()
                .bitField(key, BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType
                                .unsigned(days)).valueAt(0));
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        Long num = list.get(0);
        if (num == 0) {
            return new ArrayList<>();
        }
        List<String> res = new ArrayList<>();
        //010
        int cnt=0;
        while(cnt<31){
            if((num&1)==1){
                int x=days-cnt;
                if (x < 10) {
                    res.add(year + "-" + formatMonth + "-0" + x);
                } else {
                    res.add(year + "-" + formatMonth + "-" + x);
                }
            }
            num>>=1;
            cnt++;
        }

        return res;
    }



    @Override
    public boolean updateUserMsg(User user, String curpassword) {
        int uid=UserUtil.getCurrentUser().getUid();
        String key = CACHE_USER_KEY + uid;
        stringRedisTemplate.delete(key);
        if(curpassword!=null&&curpassword.length()!=0){
            user.setPassword(passwordEncoder.encode(curpassword));
        }
        user.setUid(uid);
        user.setEnabled(true);
        int roleName = UserUtil.getCurrentUser().getRoleName();
        user.setRoleName(roleName);
        return updateById(user);
    }

    @Override
    public List<Address> getCurrentUserAddress() {
        int uid = UserUtil.getCurrentUser().getUid();
        List<String> range = stringRedisTemplate.opsForList().range("address"+uid,0, -1);
        if(range.size()==0){
            List<Address> list = addressService.query().eq("uid", uid).orderByDesc("aid").list();
            return list;
        }
        List<Address> res=new ArrayList<>();
        for (String s : range) {
            Address address = JSONUtil.toBean(s, Address.class);
            res.add(address);
        }
        return res;
    }

    @Override
    public boolean compareTwoPassword(String password) {
        String encode = passwordEncoder.encode(password);
        return !encode.equals(UserUtil.getCurrentUser().getPassword());
    }

    @Override
    public User getResultUser(List<Address> address) {
        String username = UserUtil.getCurrentUser().getUsername();
        String phone = UserUtil.getCurrentUser().getPhone();
        int uid = UserUtil.getCurrentUser().getUid();
        String email = UserUtil.getCurrentUser().getEmail();
        String avatar=UserUtil.getCurrentUser().getAvatar();
        User user = new User();
        user.setUid(uid);
        user.setUsername(username);
        user.setPhone(phone);
        user.setAddresses(address);
        user.setEmail(email);
        user.setAddresses(address);
        user.setAvatar("http://"+OSSUtil.SUFFER_URL+avatar);
        return user;

    }
    public  User getAUserOrUserCache(Integer uid){
        String key = CACHE_USER_KEY + uid;
        String s = stringRedisTemplate.opsForValue().get(key);
        User user = null;
        if (StrUtil.isNotBlank(s)) {
            user = JSONUtil.toBean(s, User.class);
        } else {
            user = query().eq("uid", uid).one();
            if (user != null) {
                stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(user)
                        , CACHE_USER_TTL, TimeUnit.HOURS);
            }
        }
        if (user == null) {
            return null;
        }
        return user;
    }


}
