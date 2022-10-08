package com.shz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.dto.RespBean;
import com.shz.dto.Result;
import com.shz.entity.Geo;

public interface GeoService extends IService<Geo> {
    boolean addOne(Geo geo);

    Result getAroundShop(Double x, Double y, Integer start, Integer count,Integer state);

    RespBean deleteOne(Integer gid);
}
