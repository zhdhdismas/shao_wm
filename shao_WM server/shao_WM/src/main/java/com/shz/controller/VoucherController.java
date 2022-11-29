package com.shz.controller;

import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shz.dto.RespBean;
import com.shz.entity.Voucher;
import com.shz.entity.VoucherOrder;
import com.shz.service.VoucherOrderService;
import com.shz.service.VoucherService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
public class VoucherController {
    @Resource
    VoucherService voucherService;
    @Resource
    VoucherOrderService voucherOrderService;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @PostMapping("/addVoucher")
    public RespBean addVoucher(Voucher voucher){
        voucher.setImgUrl("/"+voucher.getImgUrl());
        Voucher v = voucherService.query().eq("title", voucher.getTitle()).one();
        if(v!=null){
            return new RespBean("error","添加失败,优惠券标题已存在");
        }
        boolean isSuccess = voucherService.save(voucher);
        stringRedisTemplate.opsForValue().set("seckill:stock:" + voucher.getVid(), voucher.getStock().toString());
        if(isSuccess){
            return new RespBean("success","添加成功");
        }else{
            return new RespBean("error","添加失败");
        }
    }
    @DeleteMapping("/deleteVoucher")
    public RespBean deleteVoucher(Integer vid){
        boolean isSuccess = voucherService.removeById(vid);
        stringRedisTemplate.delete("seckill:stock:"+vid);
        if(isSuccess){
            return new RespBean("success","删除成功");
        }else{
            return new RespBean("error","删除失败");
        }
    }
    @GetMapping("/allVoucher")
    public List<Voucher> queryAllVoucher(){
        List<Voucher> list = voucherService.getAllVouchers();
        return list;
    }
    @GetMapping("/getMyVoucher")
    public List<Voucher> queryMyVoucher(){
        Pair res=voucherService.queryMyVoucher();
        List<Integer> value = (List<Integer>) res.getValue();
        if(value.size()!=0)
        voucherOrderService.remove(new QueryWrapper<VoucherOrder>().in("vid",value));
        return (List<Voucher>) res.getKey();
    }


}
