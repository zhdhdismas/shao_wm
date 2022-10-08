package com.shz.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.dto.RespBean;
import com.shz.entity.Text;
import com.shz.mapper.TextMapper;
import com.shz.service.TextService;
import com.shz.utils.UserUtil;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.shz.utils.RedisConstants.USER_TEXT_KEY;
import static com.shz.utils.RedisConstants.USER_TEXT_KEY_TTL;

@Service
public class TextServiceImpl extends ServiceImpl<TextMapper, Text> implements TextService {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    public void deleteTextCache(Integer uid) {
        String key = USER_TEXT_KEY + uid;
        stringRedisTemplate.delete(key);

    }

    @Override
    public List<Text> getTheText() {
        int uid = UserUtil.getCurrentUser().getUid();
        String key = USER_TEXT_KEY + uid;
        List<String> texts = stringRedisTemplate.opsForList().range(key, 0, -1);
        if(texts.size()==0){
            List<Text> list = query().eq("uid", uid).list();
            if(list.size()!=0){
                for (Text text : list) {
                    stringRedisTemplate.opsForList().rightPush(key,JSONUtil.toJsonStr(text));
                }
                stringRedisTemplate.expire(key,USER_TEXT_KEY_TTL, TimeUnit.HOURS);
            }
            return list;
        }
        List<Text> res=new ArrayList<>();
        for (String text : texts) {
            res.add(JSONUtil.toBean(text,Text.class));
        }
        return res;
    }

    @Override
    public RespBean saveThisText(Text text) {
        int uid = UserUtil.getCurrentUser().getUid();
        text.setUid(uid);
        boolean save = save(text);
        deleteTextCache(uid);
        if (save) {
            return new RespBean("success", "保存成功");
        } else {
            return new RespBean("error", "保存失败");
        }
    }

    @Override
    public RespBean updateThisText(String createTime,String text) {
        System.out.println(createTime);
        int uid = UserUtil.getCurrentUser().getUid();
        boolean update = update(new UpdateWrapper<Text>().set("text", text).eq("createTime", createTime));
        deleteTextCache(uid);

        if (update) {
            return new RespBean("success", "保存成功");
        } else {
            return new RespBean("error", "保存失败");
        }
    }

    @Override
    public RespBean deleteThisText(String createTime) {
        int uid = UserUtil.getCurrentUser().getUid();
        boolean isSuccess = remove(new QueryWrapper<Text>().eq("createTime", createTime));
        deleteTextCache(uid);

        if(isSuccess){
            return new RespBean("success","删除成功");
        }else{
            return new RespBean("error","删除失败");
        }
    }


}
