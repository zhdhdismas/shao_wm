package com.shz.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.entity.Category;
import com.shz.mapper.CategoryMapper;
import com.shz.service.CategoryService;
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

import static com.shz.utils.RedisConstants.*;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public List<Category> getAll() {
        List<String> categorys = stringRedisTemplate.opsForList().range("category:", 0, -1);
        if (categorys.size() == 0) {
            List<Category> list = query().list();
            if (list.size() != 0) {
                for (Category category : list) {
                    stringRedisTemplate.opsForList().rightPush("category:", JSONUtil.toJsonStr(category));
                }
                stringRedisTemplate.expire("category:",CATEGORY_TTL, TimeUnit.HOURS);
            }
            return list;
        }
        List<Category> res = new ArrayList<>();
        for (String json : categorys) {
            res.add(JSONUtil.toBean(json, Category.class));
        }
        return res;
    }

    @Transactional
    @Override
    public boolean updateIdAndName(int id, String name) {
        boolean update = update(new UpdateWrapper<Category>().eq("id", id).set("name", name));
        ((CategoryService) AopContext.currentProxy()).deleteAllCategoryCache();
        return update;
    }

    @Transactional
    @Override
    public boolean addCate(String name) {
        Category cate = new Category();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        cate.setCreateTime(timestamp);
        cate.setName(name);
        boolean save = save(cate);
        ((CategoryService) AopContext.currentProxy()).deleteAllCategoryCache();
        return save;
    }

    @Transactional
    @Override
    public boolean deleteAll(String ids) {
        String[] split = ids.split(",");
        List<String> list = Arrays.asList(split);
        boolean remove = remove(new QueryWrapper<Category>().in("id", list));
        ((CategoryService) AopContext.currentProxy()).deleteAllCategoryCache();
        return remove;
    }

    @Transactional
    @Override
    public void deleteAllCategoryCache() {
        stringRedisTemplate.delete("category:");
    }
}
