package com.shz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.entity.Carousel;
import com.shz.mapper.CarouselMapper;
import com.shz.service.CarouselService;
import org.springframework.stereotype.Service;
import com.shz.utils.OSSUtil;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarouselServiceImpl extends ServiceImpl<CarouselMapper, Carousel> implements CarouselService {

    @Override
    public List<String> getImgList() {

        List<Carousel> list = query().list();
        List<String> res = new ArrayList<>();
        for (Carousel carousel : list) {
            String imgUrls="http://" + OSSUtil.SUFFER_URL + carousel.getLocation();
            String scaleAfter = imgUrls + "?x-oss-process=image/resize,m_fixed,w_650,h_400";
            res.add(scaleAfter);
        }
        return res;
    }

    @Override
    public boolean deleteAllImg() {
        List<Carousel> list = query().list();
        if (list != null) {
            for (Carousel carousel : list) {
                OSSUtil.deleteFile(carousel.getLocation().substring(1));
            }
        }
        return remove(new QueryWrapper<>());
    }


    @Override
    public boolean saveAll(String[] imgUrls) {
        boolean flag = true;
        for (String imgUrl : imgUrls) {
            Carousel ca = new Carousel();
            ca.setLocation("/" + imgUrl);
            if (!save(ca)) {
                flag = false;
                break;
            }
        }
        return flag;

    }
}
