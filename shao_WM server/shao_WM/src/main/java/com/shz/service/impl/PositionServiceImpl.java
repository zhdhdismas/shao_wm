package com.shz.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.entity.Level;
import com.shz.entity.Position;
import com.shz.mapper.PositionMapper;
import com.shz.service.LevelService;
import com.shz.service.PositionService;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.shz.utils.RedisConstants.LEVEL_TTL;

@Service
public class PositionServiceImpl extends ServiceImpl<PositionMapper, Position> implements PositionService {
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Override
    public List<Position> getAll() {
        List<String> levels = stringRedisTemplate.opsForList().range("position:", 0, -1);
        if (levels.size() == 0) {
            List<Position> list = query().list();
            if (list.size() != 0) {
                for (Position p : list) {
                    stringRedisTemplate.opsForList().rightPush("position:", JSONUtil.toJsonStr(p));
                }
                stringRedisTemplate.expire("position:",LEVEL_TTL, TimeUnit.HOURS);
            }
            return list;
        }
        List<Position> res = new ArrayList<>();
        for (String json : levels) {
            res.add(JSONUtil.toBean(json, Position.class));
        }
        return res;
    }

    @Override
    public boolean updateIdAndName(int id, String name) {
        boolean update = update(new UpdateWrapper<Position>().eq("id", id).set("name", name));
        ((PositionService) AopContext.currentProxy()).deleteAllLevelCache();
        return update;
    }


    @Override
    public void deleteAllLevelCache() {
        stringRedisTemplate.delete("position:");
    }

    @Override
    public boolean addCate(String name) {
        Position cate = new Position();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        cate.setCreateTime(timestamp);
        cate.setName(name);
        boolean save = save(cate);
        ((PositionService) AopContext.currentProxy()).deleteAllLevelCache();
        return save;
    }

    @Override
    public boolean deleteAll(String ids) {
        String[] split = ids.split(",");
        List<String> list = Arrays.asList(split);
        boolean remove = remove(new QueryWrapper<Position>().in("id", list));
        ((PositionService) AopContext.currentProxy()).deleteAllLevelCache();
        return remove;
    }
}
