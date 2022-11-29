package com.shz.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.shz.dto.Bill;
import com.shz.dto.Result;
import com.shz.entity.User;
import com.shz.service.MoneyService;
import com.shz.service.OrderService;
import com.shz.utils.DateUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/manage")
public class MoneyController {

    @Resource
    MoneyService moneyService;
    @Resource
    OrderService orderService;
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @GetMapping("/whatMoney")
    public Bill compareToDate(String startDate, String endDate,
                              @RequestParam(value = "page", defaultValue = "1")
                                      Integer page, @RequestParam(value = "count", defaultValue = "6") Integer count
            , Integer gid) {
        if (startDate.equals("undefined") || endDate.equals("undefined")) {
            List<User> list = new ArrayList<>();
            return new Bill(list, 0, 0, 0);
        }
        int sumPrice = moneyService.getSumPrice(startDate, endDate, gid);
        int costPrice = moneyService.getSumCost(startDate, endDate, gid);
        int profit = sumPrice - costPrice;
        Result res = moneyService.getRiderUser(startDate, endDate, page, count);
        return new Bill((List<User>) res.getList(), res.getTotalCount(), sumPrice, profit);
    }

    @GetMapping("/getMoneyOfYear")
    public List<Bill> getMoneyOfYear(String year, Integer gid) {
//        2022-09-01 00:00:00
        List<Bill> res = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        String base = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        if (Integer.parseInt(year) > Integer.parseInt(base.substring(0, 4))) {
            for (int i = 1; i <= 12; i++) {
                Bill b = new Bill();
                getZero(b);
                res.add(b);
            }
            return res;
        }
        String MONEY_KEY = "money:";
        boolean flag = false;
        for (int i = 1; i <= 12; i++) {
            String startDate = year + "-";
            if (i < 10) {
                startDate += "0" + i;
            } else {
                startDate += i;
            }
            String startKey = startDate;
            startDate += "-01 00:00:00";
            String endDate = year + "-";
            if ((i + 1) < 10) {
                endDate += "0" + (i + 1);
            } else if (i + 1 <= 12) {
                endDate += (i + 1);
            } else {
                endDate = Integer.parseInt(year) + 1 + "-01";
            }
            endDate += "-01 00:00:00";
            String key;
            if (gid == null) {
                key = MONEY_KEY + startKey;
            } else {
                key = MONEY_KEY + startKey + ":" + gid;
            }
            if (!startKey.equals(base)) {
                String s = stringRedisTemplate.opsForValue().get(key);
                if (StrUtil.isNotBlank(s)) {
                    res.add(JSONUtil.toBean(s, Bill.class));
                    continue;
                }
            } else {
                Bill b = new Bill();
                getResult(b, startDate, endDate, gid);
                res.add(b);
                flag = true;
                continue;
            }
            Bill b = new Bill();
            if (flag) {
                getZero(b);
            } else {
                getResult(b, startDate, endDate, gid);
            }
            res.add(b);
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(b));
        }
        return res;

    }

    @GetMapping("/getMoneyOfMonth")
    public List<Bill> getMoneyOfMonth(String year, String month, Integer gid) {
        List<Bill> res = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        String base = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int days = DateUtil.dayOfMonth(year, month);
        if (Integer.parseInt(year) > Integer.parseInt(base.substring(0, 4))
                || (Integer.parseInt(year) == Integer.parseInt(base.substring(0, 4))
                && Integer.parseInt(month) > Integer.parseInt(base.substring(5, 7)))
        ) {
            for (int i = 1; i <= days; i++) {
                Bill b = new Bill();
                getZero(b);
                res.add(b);
            }
            return res;
        }
        String MONEY_KEY = "money:day:";
        String prefix = year + "-";
        boolean flag = false;
        if (Integer.parseInt(month) < 10) {
            prefix += "0" + month;
        } else {
            prefix += month;
        }
        for (int i = 1; i <= days; i++) {
            String startDate = prefix + "-";
            if (i < 10) {
                startDate += "0" + i;
            } else {
                startDate += i;
            }
            String startKey = startDate;
            startDate += " 00:00:00";
            String endDate = prefix + "-";
            if (i == days) {
                String x = year + "-";
                if (Integer.parseInt(month) + 1 < 10) {
                    x += "0" + (Integer.parseInt(month) + 1) + "-01";
                    endDate = x;
                } else if (Integer.parseInt(month) + 1 <= 12) {
                    x += (Integer.parseInt(month) + 1) + "-01";
                    endDate = x;
                } else {
                    endDate = Integer.parseInt(year) + 1 + "-01-01";
                }
            } else {
                if ((i + 1) < 10) {
                    endDate += "0" + (i + 1);
                } else {
                    endDate += (i + 1);
                }
            }

            endDate += " 00:00:00";
            String key;
            if (gid == null) {
                key = MONEY_KEY + startKey;
            } else {
                key = MONEY_KEY + startKey + ":" + gid;
            }
            if (!startKey.equals(base)) {
                String s = stringRedisTemplate.opsForValue().get(key);
                if (StrUtil.isNotBlank(s)) {
                    res.add(JSONUtil.toBean(s, Bill.class));
                    continue;
                }
            } else {
                Bill b = new Bill();
                getResult(b, startDate, endDate, gid);
                res.add(b);
                flag = true;
                continue;
            }
            Bill b = new Bill();
            if (flag) {
                getZero(b);
            } else {
                getResult(b, startDate, endDate, gid);
            }
            res.add(b);
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(b));
        }
        return res;
    }

    private void getResult(Bill bill, String startDate, String endDate, Integer gid) {
        int sumPrice = moneyService.getSumPrice(startDate, endDate, gid);
        int costPrice = moneyService.getSumCost(startDate, endDate, gid);
        int profit = sumPrice - costPrice;
        bill.setSumPrice(sumPrice);
        bill.setProfit(profit);
        Integer count;
        if (gid == null) {
            count = orderService.query().eq("status", 2)
                    .between("publishDate", startDate, endDate).count();
        } else {
            count = orderService.query().eq("status", 2).eq("gid", gid)
                    .between("publishDate", startDate, endDate).count();
        }
        bill.setTotalCount(count);
    }

    void getZero(Bill b) {
        b.setProfit(0);
        b.setTotalCount(0);
        b.setSumPrice(0);
    }

}
