package com.shz.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shz.dto.RespBean;
import com.shz.entity.Voucher;
import com.shz.entity.VoucherOrder;
import com.shz.service.VoucherOrderService;
import com.shz.service.VoucherService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class VoucherOrderController {

    @Resource
    VoucherOrderService voucherOrderService;
    @PostMapping("/seckill/{id}")
    public RespBean seckillVoucher(@PathVariable("id") Long voucherId) {
        return voucherOrderService.seckillVoucher(voucherId);
    }
    @DeleteMapping("/deleteMyVouchers")
    public RespBean deleteMyVouchers(Integer[] vids){
        if(vids==null||vids.length==0) return new RespBean("success","");
        List<Integer> list = new ArrayList<>(Arrays.asList(vids));
        // QueryWrapper in这个语法最好用list来入参
        boolean isSuccess = voucherOrderService.remove(new QueryWrapper<VoucherOrder>().in("vid", list));
        if(isSuccess){
            return new RespBean("success","");
        }else{
            return new RespBean("error","");
        }
    }

}
