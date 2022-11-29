package com.shz.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shz.dto.Result;
import com.shz.entity.User;
import com.shz.service.MoneyService;
import com.shz.service.OrderService;
import com.shz.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Service
public class MoneyServiceImpl implements MoneyService {
    @Resource
    OrderService orderService;
    @Resource
    UserService userService;

    @Override
    public Result getRiderUser(String startTime, String endTime, Integer page, Integer count) {
        Page<User> pages = userService.query().eq("rolename", 1).page(new Page<>(page, count));
        for (User user : pages.getRecords()) {
            int counts = orderService.query().eq("ucid", user.getUid())
                    .eq("status", 2).between("publishDate", startTime, endTime).count();
            user.setCount(counts);
        }
        return new Result(pages.getRecords(), (int)pages.getTotal());


    }

    public static Timestamp CovertStrTODate(String str) {
        Timestamp ts = null;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setLenient(false);
        try {
            ts = new Timestamp(format.parse(str).getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return ts;
    }

    @Override
    public int getSumPrice(String startDate, String endDate,Integer gid) {
        return orderService.selectSumPrice(startDate, endDate,gid);
    }

    @Override
    public int getSumCost(String startDate, String endDate,Integer gid) {
        return orderService.selectSumCost(startDate, endDate,gid);
    }
}
