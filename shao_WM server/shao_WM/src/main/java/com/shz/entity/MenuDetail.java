package com.shz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class MenuDetail implements Serializable {
    @TableId(type=IdType.AUTO)
    private Integer id;
    private String zhuliao;
    private String weidao;
    private String way;
    private String time;
    private Double rate;
    private Integer count;
    private Integer mid;
}
