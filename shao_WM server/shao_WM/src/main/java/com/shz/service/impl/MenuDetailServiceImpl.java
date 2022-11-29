package com.shz.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.entity.MenuDetail;
import com.shz.mapper.MenuDetailMapper;
import com.shz.service.MenuDetailService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.shz.utils.RedisConstants.CACHE_MENU_DETAIL_KEY;

@Service
public class MenuDetailServiceImpl extends ServiceImpl<MenuDetailMapper,MenuDetail> implements MenuDetailService {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Override
    public MenuDetail getAMenuDetail(Integer mid) {
        String key=CACHE_MENU_DETAIL_KEY+mid;
        String s = stringRedisTemplate.opsForValue().get(key);
        if(StrUtil.isNotBlank(s)){
            return JSONUtil.toBean(s,MenuDetail.class);
        }
        MenuDetail aMenuDetail = query().eq("mid",mid).one();
        if(aMenuDetail==null){
            return null;
        }
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(aMenuDetail));
        return aMenuDetail;
    }
}
