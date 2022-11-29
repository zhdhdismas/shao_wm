package com.shz.controller;

import com.shz.dto.RespBean;
import com.shz.entity.Carousel;
import com.shz.service.CarouselService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/img")
public class ImgController {
    @Resource
    CarouselService carouselService;
    @GetMapping("/homepageImg")
    public List<String> getCarouselLocation(){
          return carouselService.getImgList();
    }
    @DeleteMapping("/deleteAll")
    public RespBean deleteAllImg(){

        boolean res = carouselService.deleteAllImg();
        return new RespBean(res?"success":"error","");
    }
    @PostMapping("/saveAll")
    public RespBean saveAllImg(String[] imgUrls){

        boolean res = carouselService.saveAll(imgUrls);
        return new RespBean(res?"success":"error","");

    }

}
