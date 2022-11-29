package com.shz.controller;

import cn.hutool.json.JSONUtil;
import com.shz.dto.RespBean;
import com.shz.dto.Result;
import com.shz.entity.Order;
import com.shz.service.OrderService;
import com.shz.utils.RedisIdWorker;
import com.shz.utils.UserUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
public class OrderController {
    @Resource
    OrderService orderService;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    RedisIdWorker redisIdWorker;
    //1.   骑手订单
    @GetMapping("/rider/getAllOrderNone")
    public Map<String, Object> getAllOrderNone(@RequestParam(value = "page", defaultValue = "1") Integer page
            , @RequestParam(value = "count", defaultValue = "6") Integer count) {
        Map<String, Object> map = new HashMap<>();
        Result res = orderService.getAllOrderNone(page, count);
        map.put("orders", res.getList());
        map.put("totalCount", res.getTotalCount());
        return map;
    }

    @PutMapping("/rider/receiveOrder")
    public RespBean receiveOrder(Long oid) {
        if (orderService.updateStateOrder(oid)) {
            return new RespBean("success", "接单成功");
        }
        return new RespBean("error", "接单失败,这单已被抢了");
    }

    @PutMapping("/rider/confirmOrder")
    public RespBean confirmOrder(Long oid) {
        if (orderService.confirmOrder(oid)) {
            return new RespBean("success", "确认成功");
        }
        return new RespBean("error", "服务器错误");
    }

    @GetMapping("/rider/getMyCurReceiveOrder")
    public Map<String, Object> getMyCurReceiveOrder(@RequestParam(value = "page", defaultValue = "1") Integer page
            , @RequestParam(value = "count", defaultValue = "6") Integer count) {
        Map<String, Object> map = new HashMap<>();
        Result res = orderService.getMyCurReceiveOrder(page, count);
        map.put("orders", res.getList());
        map.put("totalCount", res.getTotalCount());

        return map;

    }

    @GetMapping("/rider/getMyReceiveOrderAchieved")
    public Map<String, Object> getMyReceiveOrderAchieved(@RequestParam(value = "page", defaultValue = "1") Integer page
            , @RequestParam(value = "count", defaultValue = "6") Integer count) {
        Map<String, Object> map = new HashMap<>();
        Result res = orderService.getMyReceiveOrderAchieved(page, count);
        map.put("orders", res.getList());
        map.put("totalCount", res.getTotalCount());
        return map;

    }
    @GetMapping("/rider/getMyMonthAchieved")
    public Integer getOrderTotalCount(){
        int uid = UserUtil.getCurrentUser().getUid();
        LocalDateTime now=LocalDateTime.now();
        String base = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String startTime=base+"-01 00:00:00";
        String endTime;
        String month=base.substring(5,7);
        String year=base.substring(0,4);
        if(Integer.parseInt(month)+1>=10){
            endTime=year+"-"+(Integer.parseInt(month)+1);
        }else{
            endTime=year+"-0"+(Integer.parseInt(month)+1);
        }
        endTime+="-01 00:00:00";
        return orderService.query().eq("ucid", uid).eq("status",2).between("publishDate", startTime, endTime).count();

    }

    //2.  管理员订单
    @GetMapping("/manage/getAllOrders")
    public Map<String, Object> getAllOrders(@RequestParam(value = "page", defaultValue = "1") Integer page
            , @RequestParam(value = "count", defaultValue = "6") Integer count,String keyword,Integer status) {
        Map<String, Object> map = new HashMap<>();
        Result res = orderService.getAllOrder(page, count,keyword,status);
        map.put("orders", res.getList());
        map.put("totalCount", res.getTotalCount());
        return map;
    }


    //  3.  用户订单
    @PostMapping("/addOrder")
    public RespBean addOneOrder(@RequestBody Order order) {
        if (order.getCustomer().length() == 0
                || order.getAddress().length() == 0
                || order.getPhone().length() != 11 || order.getContent().length() == 0) {
            return new RespBean("error", "订单信息不得为空,请输入有效信息");
        }
        long id = redisIdWorker.nextId("my_order");
        order.setOid(id);
        int uid = UserUtil.getCurrentUser().getUid();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Random random = new Random();
        int i = random.nextInt(40);
        order.setLimitTime(i + 20);
        order.setPublishDate(timestamp);
        order.setUid(uid);
        order.setStatus(0);
        order.setUcid(-1);
        orderService.addOnOrderToRedis(order);
        return new RespBean("success",id);
    }
    @GetMapping("/getAExpire/{oid}")
    public Long getAExpire(@PathVariable Long oid){
        String key="my_order:"+oid;
        Long expire = stringRedisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
        return expire;

    }

    @GetMapping("/getMyOrders")
    public Map<String, Object> getMyOrders() {
        Map<String, Object> map = new HashMap<>();
        List<Order> myOrders = orderService.getMyCurOrder();
        map.put("orders", myOrders);
        return map;
    }

    @GetMapping("/myHistoryOrders")
    public Map<String, Object> getMyHistoryOrders(@RequestParam(value = "page", defaultValue = "1") Integer page
            , @RequestParam(value = "count", defaultValue = "6") Integer count) {
        Map<String, Object> map = new HashMap<>();
        Result res = orderService.getMyOrder(page, count);
        map.put("orders", res.getList());
        map.put("totalCount", res.getTotalCount());
        return map;
    }
}
