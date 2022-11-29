package com.shz.controller;

import cn.hutool.json.JSONUtil;
import com.shz.dto.RespBean;
import com.shz.dto.Result;
import com.shz.entity.Geo;
import com.shz.service.GeoService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/geo")
public class GeoController {

    @Resource
    GeoService geoService;
    @PostMapping("/addOne")
    public RespBean addOne(Geo geo){
        boolean save = geoService.addOne(geo);
        if(save){
            return new RespBean("success","添加成功");
        }else{
            return new RespBean("error","添加失败");
        }
    }
    @GetMapping("/aroundShop")
    public Result aroundShop(Double x, Double y,Integer start,Integer count,Integer state){
        return geoService.getAroundShop(x,y,start,count,state);

    }
    @DeleteMapping("/deleteOne/{gid}")
    public RespBean deleteOne(@PathVariable Integer gid){
        return geoService.deleteOne(gid);
    }
}
