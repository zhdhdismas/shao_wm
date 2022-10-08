package com.shz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Carousel {
    @TableId(type = IdType.AUTO)
    private int caid;
    private String location;
}
