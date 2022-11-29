package com.shz.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class Blog implements Serializable {

    @JSONField(serializeUsing= ToStringSerializer.class)
    @TableId(type = IdType.INPUT)
    private Long bid;
    private int uid;
    private String blogContent;
    private String blogTitle;
    private Timestamp createTime;
    private String mdContent;
    private String code;
    private int liked;
    @TableField(exist = false)
    private User user;
    @TableField(exist = false)
    private boolean like;

}
