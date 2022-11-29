package com.shz.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.dto.RespBean;
import com.shz.dto.Result;
import com.shz.entity.Geo;
import com.shz.mapper.GeoMapper;
import com.shz.service.GeoService;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeoServiceImpl extends ServiceImpl<GeoMapper, Geo> implements GeoService {
    @Resource
    StringRedisTemplate stringRedisTemplate;


    @Override
    public boolean addOne(Geo geo) {
        boolean save = save(geo);
        stringRedisTemplate.delete("geo:all:");
        stringRedisTemplate.delete("geo:search:");
        return save;

    }

    @Override
    public Result getAroundShop(Double x, Double y, Integer start, Integer count, Integer state) {
        if (x == null || y == null) {
            List<String> range;
            if (start == null || count == null) {
                range = stringRedisTemplate.opsForList().range("geo:all:", 0, -1);
            } else {
                range = stringRedisTemplate.opsForList()
                        .range("geo:all:", (long) (start - 1) * count, (long) (start - 1) * count + count - 1);
            }
            if (range.size() == 0) {
                if(start==null||count==null){
                    List<Geo> list = query().list();
                    if(list.size()!=0){
                        for (Geo geo : list) {
                            stringRedisTemplate.opsForList().rightPush("geo:all:", JSONUtil.toJsonStr(geo));
                        }
                    }
                    return new Result(list,null);
                }
                Page<Geo> pages = query().page(new Page<>(start, count));
                List<Geo> records = pages.getRecords();
                if (records.size() != 0) {
                    List<Geo> list = query().list();
                    for (Geo geo : list) {
                        stringRedisTemplate.opsForList().rightPush("geo:all:", JSONUtil.toJsonStr(geo));
                    }
                }
                return new Result(records, (int) pages.getTotal());
            }
            List<Geo> res = new ArrayList<>();
            for (String s : range) {
                Geo geo = JSONUtil.toBean(s, Geo.class);
                res.add(geo);
            }
            return new Result(res, null);
        }
        int distance = state == 0 ? 5000 : 10000;
        int from = (start - 1) * count;
        int end = start * count;
        GeoResults<RedisGeoCommands.GeoLocation<String>> geo = stringRedisTemplate.opsForGeo().search("geo:search:",
                GeoReference.fromCoordinate(x, y),
                new Distance(distance),
                RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end)
        );
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = geo.getContent();
        if (list.size() == 0) {
            List<Geo> list1 = query().list();
//            for (Geo geo1 : list1) {
//                stringRedisTemplate.opsForGeo().add("geo:search:",
//                        new Point(geo1.getLongitude(), geo1.getLatitude()), geo1.getName());
//            }
            List<RedisGeoCommands.GeoLocation<String>> locations=new ArrayList<>(list1.size());
            for(Geo geo1:list1){
                locations.add(new RedisGeoCommands.GeoLocation<>
                        (geo1.getName(),new Point(geo1.getLongitude(),geo1.getLatitude())));
            }
            stringRedisTemplate.opsForGeo().add("geo:search:",locations);
            GeoResults<RedisGeoCommands.GeoLocation<String>> geo2 = stringRedisTemplate.opsForGeo().search("geo:search:",
                    GeoReference.fromCoordinate(x, y),
                    new Distance(distance),
                    RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end)
            );
            List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list2 = geo2.getContent();
            List<Geo> res = new ArrayList<>();
            list2.stream().skip(from).forEach(result -> {
                        Geo g = new Geo();
                        String name = result.getContent().getName();
                        g.setName(name);
                        g.setDistance(result.getDistance().getValue());
                        res.add(g);
                    }
            );
            return new Result(res, null);
        }
        List<Geo> res = new ArrayList<>();
        list.stream().skip(from).forEach(result -> {
                    Geo g = new Geo();
                    String name = result.getContent().getName();
                    g.setName(name);
                    g.setDistance(result.getDistance().getValue());
                    res.add(g);
                }
        );
        return new Result(res, null);

    }

    @Override
    public RespBean deleteOne(Integer gid) {
        boolean isSuccess = removeById(gid);
        stringRedisTemplate.delete("geo:all:");
        stringRedisTemplate.delete("geo:search:");
        if (isSuccess) {
            return new RespBean("success", "成功删除");
        } else {
            return new RespBean("error", "删除失败");
        }
    }
}
