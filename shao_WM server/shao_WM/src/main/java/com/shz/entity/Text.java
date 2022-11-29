package com.shz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

@Data
public class Text implements Serializable {
    @TableId(type = IdType.AUTO)
    private int tid;
    private Date createTime;
    private String text;
    private int uid;
}
