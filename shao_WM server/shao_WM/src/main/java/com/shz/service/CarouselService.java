package com.shz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.entity.Carousel;

import java.util.List;

public interface CarouselService extends IService<Carousel> {

    List<String> getImgList();

    boolean deleteAllImg();

    boolean saveAll(String[] imgUrls);
}
