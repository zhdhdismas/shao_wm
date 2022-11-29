package com.shz.service.impl;

import cn.hutool.core.io.resource.StringResource;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.entity.Address;
import com.shz.mapper.AddressMapper;
import com.shz.service.AddressService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.shz.utils.UserUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.shz.utils.RedisConstants.ADDRESS_TTL;


@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean selAddressExist(String address, Integer uid) {
        List<String> ranges = stringRedisTemplate.opsForList().range("address:" + uid, 0, -1);
        if(ranges.size()==0){
            List<Address> list = query().eq("uid", uid).list();
            if(list.size()!=0){
                boolean exist=false;
                for(Address add:list){
                    if(Objects.equals(add.getAddress(), address)){
                        exist=true;
                    }
                    stringRedisTemplate.opsForList().rightPush("address:"+uid,add.getAddress());
                }
                stringRedisTemplate.expire("address:"+uid,ADDRESS_TTL, TimeUnit.HOURS);
                return exist;
            }
        }
        for (String s : ranges) {
            if(s.equals(address)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addOneAddress(String address1) {
        int uid = UserUtil.getCurrentUser().getUid();
//        stringRedisTemplate.opsForList().rightPush("address:"+uid,address1);
        Address address = new Address();
        address.setAddress(address1);
        address.setUid(uid);
        boolean save = save(address);
        stringRedisTemplate.delete("address:"+uid);
        return save;
    }
}
