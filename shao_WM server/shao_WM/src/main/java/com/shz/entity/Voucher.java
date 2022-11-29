package com.shz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class Voucher implements Serializable {
    @TableId(type=IdType.AUTO)
    private Integer vid;
    private Integer stock;
    private String title;
    private String description;
    private Timestamp createTime;
    private Timestamp endTime;
    private Timestamp deadTime;
    private String imgUrl;
    private Integer price;
}
