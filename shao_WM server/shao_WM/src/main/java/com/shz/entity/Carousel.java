package com.shz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class Carousel implements Serializable {
    @TableId(type = IdType.AUTO)
    private int caid;
    private String location;
}
