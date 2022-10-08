package com.shz.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.dto.Result;
import com.shz.entity.Order;
import com.shz.entity.User;
import com.shz.mapper.OrderMapper;
import com.shz.service.OrderService;
import com.shz.service.UserService;
import org.springframework.aop.framework.AopContext;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shz.utils.UserUtil;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {


    @Resource
    UserService userService;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    StringRedisTemplate stringRedisTemplate;



    @Override
    public Result getMyOrder(Integer page, Integer count) {
        int uid = UserUtil.getCurrentUser().getUid();
        Page pages = query().eq("uid", uid).orderByDesc("publishDate").page(new Page(page, count));
        return new Result(pages.getRecords(),(int)pages.getTotal());

    }



    @Override
    public List<Order> getMyCurOrder() {
        int uid = UserUtil.getCurrentUser().getUid();
        List<String> keyList = new ArrayList<>();
        try {
            redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
                Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match("my_order:*")
                        .count(Integer.MAX_VALUE).build());
                while (cursor.hasNext()) {
                    keyList.add(new String(cursor.next()));
                }
                return null;
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        List<Order> list = query().eq("uid", uid).eq("status", 0).or().eq("status", 1).list();
        for (Order order : list) {
            order.setPay("已支付");
        }
        for (String s : keyList) {
            String s1 = stringRedisTemplate.opsForValue().get(s);
            Order order = JSONUtil.toBean(s1, Order.class);
            if(order.getUid()==uid){
                order.setPay("未支付");
                list.add(order);
            }

        }
        return list;

    }

    // 2. 骑手订单


    @Override
    public Result getAllOrderNone(Integer page, Integer count) {
        Page<Order> pages = query().eq("ucid", -1).orderByDesc("publishDate").page(new Page<>(page, count));
        return new Result(pages.getRecords(), (int)pages.getTotal());
    }

    @Override
    public boolean updateStateOrder(Long oid) {
        int uid = UserUtil.getCurrentUser().getUid();
        synchronized (Integer.valueOf(uid).toString().intern()) {
           return  ((OrderService) AopContext.currentProxy()).updateState(oid,uid);
        }
    }

    @Transactional
    @Override
    public boolean updateState(Long oid,int uid){
        int count = query().ne("ucid", -1).eq("oid", oid).count();
        if (count > 0) {
            return false;
        }
        boolean update = update(new UpdateWrapper<Order>().set("ucid", uid).setSql("status=1").eq("oid", oid));
        return update;
    }

    @Override
    public void addOnOrderToRedis(Order order) {
        int uid = UserUtil.getCurrentUser().getUid();
        String key = "my_order:" + order.getOid();
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(order));
        stringRedisTemplate.expire(key,30, TimeUnit.MINUTES);
    }


    @Override
    public Result getMyCurReceiveOrder(Integer page, Integer count) {
        int uid = UserUtil.getCurrentUser().getUid();
        Page<Order> pages = query().eq("ucid", uid)
                .eq("status", 1)
                .orderByDesc("publishDate")
                .page(new Page<>(page, count));

        return new Result(pages.getRecords(), (int)pages.getTotal());

    }



    @Override
    public Result getMyReceiveOrderAchieved(Integer page, Integer count) {
        int uid = UserUtil.getCurrentUser().getUid();
        Page<Order> pages = query().eq("ucid", uid).eq("status", 2)
                .orderByDesc("publishDate")
                .page(new Page<>(page, count));
        return new Result(pages.getRecords(), (int)pages.getTotal());
    }

    @Override
    public boolean confirmOrder(Long oid) {
        return update(new UpdateWrapper<Order>().setSql("status=2").eq("oid", oid));
    }

    //    3. 管理员订单
    @Override
    public Result getAllOrder(Integer page, Integer count,String keyword,Integer status) {
        Page<Order> pages;
        if(status==-1){
            pages = query().orderByDesc("publishDate").like("geoname",keyword).page(new Page<>(page, count));
        }else{
            pages=query().orderByDesc("publishDate").eq("status",status).like("geoname",keyword).page(new Page<>(page, count));
        }
        for (Order order : pages.getRecords()) {
            int st = order.getStatus();
            if (st!=0) {
                int ucid = order.getUcid();
                User user = userService.getAUserOrUserCache(ucid);
                //1:配送中 2:已送达
                order.setUser(user);
            } else {
                //0:暂未配送
                User us = new User();
                us.setPhone("无");
                us.setUsername("无");
                order.setUser(us);
            }
        }
        return new Result(pages.getRecords(),(int)pages.getTotal());
    }


    @Override
    public int selectSumPrice(String startDate, String endDate,Integer gid) {
        QueryWrapper<Order> q;
        if(gid==null){
            q = new QueryWrapper<Order>()
                    .select("ifnull(sum(price),0) as sum")
                    .eq("status", 2)
                    .between("publishDate",startDate,endDate);
        }else{
            q=new QueryWrapper<Order>()
                    .select("ifnull(sum(price),0) as sum")
                    .eq("status", 2).eq("gid",gid)
                    .between("publishDate",startDate,endDate);
        }
        Map<String, Object> map = getMap(q);
        BigDecimal sumCount= (BigDecimal) map.get("sum");
        return Integer.parseInt(String.valueOf(sumCount));

    }

    @Override
    public int selectSumCost(String startDate, String endDate,Integer gid) {
        QueryWrapper<Order> q;
        if (gid==null){
            q= new QueryWrapper<Order>()
                    .select("ifnull(sum(cost),0) as cost")
                    .eq("status", 2)
                    .between("publishDate",startDate,endDate);
        }else{
            q=new QueryWrapper<Order>()
                    .select("ifnull(sum(cost),0) as cost")
                    .eq("status", 2).eq("gid",gid)
                    .between("publishDate",startDate,endDate);
        }
        Map<String, Object> map = getMap(q);
        BigDecimal sumCount= (BigDecimal) map.get("cost");
        return Integer.parseInt(String.valueOf(sumCount));

    }
}
