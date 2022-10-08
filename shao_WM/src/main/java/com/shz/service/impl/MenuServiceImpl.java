package com.shz.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.dto.RespBean;
import com.shz.dto.Result;
import com.shz.entity.Category;
import com.shz.entity.Menu;
import com.shz.mapper.MenuMapper;
import com.shz.service.CategoryService;
import com.shz.service.MenuService;
import com.shz.utils.RedisFurryAndPageQueryUtil;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import com.shz.utils.OSSUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static com.shz.utils.RedisConstants.*;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    RedisFurryAndPageQueryUtil redisFurryAndPageQueryUtil;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    CategoryService categoryService;

    @Override
    public Result getMenuByState(Integer state, Integer page, Integer count, String keywords) {
        String tableName = "menu";
        Page<Menu> pages1 = redisFurryAndPageQueryUtil.find(keywords, page, count, tableName + ":" + state, Menu.class);
        if (pages1.getTotal() == 0) {
            Page<Menu> pages2 = query().eq("tid", state)
                    .like("title", keywords)
                    .orderByDesc("price").page(new Page<Menu>(page, count));
            List<Menu> records = pages2.getRecords();
            for (Menu menu : records ) {
//            menu.setLocation("http://localhost:8081" + menu.getLocation());
                menu.setLocation("http://" + OSSUtil.SUFFER_URL + menu.getLocation());
            }
            if (records.size() != 0) {
                List<Menu> pages3 = query().eq("tid", state)
                        .orderByDesc("price").list();
                for (Menu menu : pages3) {
                    redisTemplate.opsForHash().put(tableName + ":" + menu.getTid(),
                            menu.getTitle(), JSONUtil.toJsonStr(menu));
                }
                //MENUS_TTL随机数是解决缓存穿透的方法之一
                //history,menu键的时间必须同步,否则在redis查不到数据
//                redisTemplate.expire(tableName + ":" + state, MENUS_TTL, TimeUnit.HOURS);
            }
            return new Result(records, (int) pages2.getTotal());
        }
        List<Menu> list = new ArrayList<>();
        for (Object o : pages1.getRecords().toArray()) {
            JSONObject jsonObject = JSONUtil.parseObj(o);
            Menu menu = jsonObject.toBean(Menu.class);
            menu.setLocation("http://" + OSSUtil.SUFFER_URL + menu.getLocation());
            list.add(menu);
        }
//        Collections.sort(list, Comparator.comparingInt(Menu::getPrice));
        return new Result(list, (int) pages1.getTotal());

    }

    @Override
    @Transactional
    public boolean deleteMenus(Integer[] mids) {

        boolean remove = remove(new QueryWrapper<Menu>().in("mid", mids));
        for (Integer mid : mids) {
            ((MenuService) AopContext.currentProxy()).deleteOneCache(mid);
        }
        ((MenuService) AopContext.currentProxy()).deleteAllCache();
        return remove;
    }

    @Override
    @Transactional
    public boolean deleteThisMenu(Integer mid) {
        Menu one = query().eq("mid", mid).one();
        if (one == null) {
            return false;
        }
        boolean remove = removeById(mid);
        ((MenuService) AopContext.currentProxy()).deleteOneCache(mid);
        ((MenuService) AopContext.currentProxy()).deleteAllCache();
        return remove;


    }

    @Override
    @Transactional
    public boolean updateMenu(Menu menu) {

        boolean remove = updateById(menu);
        ((MenuService) AopContext.currentProxy()).deleteOneCache(menu.getMid());
        ((MenuService) AopContext.currentProxy()).deleteAllCache();
        return remove;

    }

    @Override
    public boolean titleExist(String title) {
        Menu menu = query().eq("title", title).one();
        if (menu != null) return true;
        return false;
    }

    @Override
    public Menu getAMenu(Integer mid) {
        String key = CACHE_MENU_KEY + mid;
        String menuJson = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(menuJson)) {
            Menu menu = JSONUtil.toBean(menuJson, Menu.class);
            return menu;
        }
        Menu menu = query().eq("mid", mid).one();
        if (menu == null) {
            return null;
        }
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(menu));
        return menu;
    }

    @Override
    @Transactional
    public boolean addOneMenu(Menu menu) {
        menu.setLocation("/" + menu.getLocation());
        boolean save = save(menu);
        ((MenuService) AopContext.currentProxy()).deleteAllCache();
        return save;

    }

    @Transactional
    @Override
    public void deleteAllCache() {
        //keys命令在高并发场景会造成线程堵塞,post,get请求很慢
        List<String> keyList = new ArrayList<>();
        try {
            redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
                Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match("history:menu*")
                        .count(Integer.MAX_VALUE).build());
                while (cursor.hasNext()) {
                    keyList.add(new String(cursor.next()));
                }
                return null;
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        redisTemplate.delete(keyList);

        List<Category> all = categoryService.getAll();
        for (Category c: all) {
            redisTemplate.delete("menu:"+c.getId());
        }

    }

    @Override
    public void deleteOneCache(int mid) {
        redisTemplate.delete(CACHE_MENU_KEY + mid);
    }


    @Override
    public boolean ThisCategoryMenusNone(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        Integer count = query().in("tid", list).count();
        if(count>0){
            return false;
        }
        return true;
    }

    @Override
    public RespBean updateStatus(Integer mid, Boolean status) {
        boolean update = update(new UpdateWrapper<Menu>().eq("mid", mid).set("status", status));
        ((MenuService) AopContext.currentProxy()).deleteAllCache();
        if(update){
            return new RespBean("success","修改成功");
        }else{
            return new RespBean("error","修改失败");
        }
    }
}
