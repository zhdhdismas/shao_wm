package com.shz;

import com.shz.entity.Menu;
import com.shz.entity.MenuDetail;
import com.shz.mapper.MenuMapper;
import com.shz.service.CategoryService;
import com.shz.service.MenuDetailService;
import com.shz.service.MenuService;
import com.shz.service.OrderService;
import com.shz.utils.RedisFurryAndPageQueryUtil;
import com.shz.utils.RedisIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.python.util.PythonInterpreter;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


@Slf4j
@SpringBootTest(classes = ShaoWmApplication.class)
// 让 JUnit 运行 Spring 的测试环境， 获得 Spring 环境的上下文的支持
@RunWith(SpringRunner.class)
public class MethodTestUtils {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private RedisFurryAndPageQueryUtil redisFurryAndPageQueryUtil;
    @Resource
    MenuMapper menuMapper;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    CategoryService categoryService;
    @Resource
    RedisIdWorker redisIdWorker;
    @Resource
    OrderService orderService;

    @Test
    public void testRedisPage() {
//        String tableName = "menu";
//        List<Menu> menus = menuMapper.selectList(null);
//        System.out.println(menus.size());
//        for (Menu menu : menus) {
//            redisTemplate.opsForHash().put(tableName, menu.getTitle(), JSONUtil.toJsonStr(menu));
//        }
//        Page<Menu> page = redisFurryAndPageQueryUtil.find("", 1, 10, tableName, Menu.class);
//        for (Object o : page.getRecords().toArray()) {
//            JSONObject jsonObject = JSONUtil.parseObj(o);
//            Menu menu = jsonObject.toBean(Menu.class);
//        }
        List<String> shz = stringRedisTemplate.opsForList().range("shzzz", 0, 1000);
        System.out.println(shz.size());
        for (String s : shz) {
            System.out.println(s);
        }

    }


    @Resource
    MenuDetailService menuDetailService;

    @Resource
    MenuService menuService;

    @Test
    public void test() {
        List<Menu> list = menuService.query().select("introduction","mid").list();
        list.forEach(item->{
            String introduction = item.getIntroduction();
            int i = introduction.indexOf(":");
            int j = introduction.indexOf("味道");
            String zhuliao = introduction.substring(i + 1, j);
            int k = introduction.indexOf("做菜方式");
            String weidao = introduction.substring(j + 3, k - 1);
            String way = introduction.substring(k + 5, k + 7);
            int s = introduction.indexOf("做菜所需时间:");
            String time=introduction.substring(s+7);
            MenuDetail x=new MenuDetail();
            x.setCount(0);
            x.setRate((double) 0);
            x.setWeidao(weidao);
            x.setZhuliao(zhuliao);
            x.setWay(way);
            x.setTime(time);
            x.setMid(item.getMid());
            menuDetailService.save(x);
        });
    }


}

