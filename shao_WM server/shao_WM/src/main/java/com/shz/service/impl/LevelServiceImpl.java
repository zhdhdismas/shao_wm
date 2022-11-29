package com.shz.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.entity.Category;
import com.shz.entity.Level;
import com.shz.mapper.LevelMapper;
import com.shz.service.CategoryService;
import com.shz.service.LevelService;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.shz.utils.RedisConstants.LEVEL_TTL;

@Service
@Transactional
public class LevelServiceImpl extends ServiceImpl<LevelMapper, Level> implements LevelService {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Override
    public List<Level> getAll() {
        List<String> levels = stringRedisTemplate.opsForList().range("level:", 0, -1);
        if (levels.size() == 0) {
            List<Level> list = query().list();
            if (list.size() != 0) {
                for (Level level : list) {
                    stringRedisTemplate.opsForList().rightPush("level:", JSONUtil.toJsonStr(level));
                }
                stringRedisTemplate.expire("level:",LEVEL_TTL, TimeUnit.HOURS);
            }
            return list;
        }
        List<Level> res = new ArrayList<>();
        for (String json : levels) {
            res.add(JSONUtil.toBean(json, Level.class));
        }
        return res;
    }

    @Override
    public boolean updateIdAndName(int id, String name) {
        boolean update = update(new UpdateWrapper<Level>().eq("id", id).set("levelName", name));
        ((LevelService) AopContext.currentProxy()).deleteAllLevelCache();
        return update;
    }

    @Override
    public boolean addCate(String name) {
        Level cate = new Level();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        cate.setCreateTime(timestamp);
        cate.setLevelName(name);
        boolean save = save(cate);
        ((LevelService) AopContext.currentProxy()).deleteAllLevelCache();
        return save;
    }

    @Override
    public boolean deleteAll(String ids) {
        String[] split = ids.split(",");
        List<String> list = Arrays.asList(split);
        boolean remove = remove(new QueryWrapper<Level>().in("id", list));
        ((LevelService) AopContext.currentProxy()).deleteAllLevelCache();
        return remove;
    }

    @Override
    public void deleteAllLevelCache() {
        stringRedisTemplate.delete("level:");
    }
}
