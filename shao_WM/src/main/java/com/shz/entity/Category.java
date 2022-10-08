package com.shz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class Category {
    @TableId(type= IdType.AUTO)
    private int id;
    private String name;
    private Timestamp createTime;
}
