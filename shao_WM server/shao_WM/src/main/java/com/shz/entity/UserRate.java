package com.shz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserRate implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private Integer mid;
    private Integer rate;
}
