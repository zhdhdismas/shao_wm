package com.shz;

import cn.hutool.json.JSONUtil;
import com.shz.dto.Result;
import com.shz.entity.*;
import com.shz.mapper.MenuMapper;
import com.shz.service.*;
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
import java.sql.Timestamp;
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
    @Resource
    BlogService blogService;
    @Resource
    StaffService staffService;

    @Test
    public void testRedisPage() {
        Result s = staffService.getStaffByName("s", 0, 5);
        System.out.println(s.getTotalCount());
        System.out.println(s.getList().size());
        List<Staff> list = (List<Staff>) s.getList();
        for (Staff staff : list) {
            System.out.println(staff);
        }

    }


    @Resource
    MenuDetailService menuDetailService;

    @Resource
    MenuService menuService;

    @Test
    public void test() {
        int[] arr={1,2,3,4,5};
        dfs(arr);
        for (int i : arr) {
            System.out.println(i);
        }
        List<String> s= stringRedisTemplate.opsForList().range("address:38", 0, -1);
        for (String s1 : s) {
            System.out.println(s1);
        }
    }

    private void dfs(int[] arr) {
        int[] x=arr;
        x[3]=2;
    }
    @Test
    public void test2(){
        ChatMsg msg=new ChatMsg();
        String m="{a:sdf}";
        String m1="{from:'sdf'}";
        String m2="{'from':'sdf'}";
        ChatMsg c = JSONUtil.toBean(m, ChatMsg.class);
        ChatMsg c2 = JSONUtil.toBean(m, ChatMsg.class);
        System.out.println(c.toString());
        System.out.println(c2.toString());
    }





}

