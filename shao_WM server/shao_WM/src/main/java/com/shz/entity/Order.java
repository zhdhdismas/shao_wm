package com.shz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.sql.Timestamp;
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("orders")
public class Order implements Serializable {
    @TableId(type = IdType.INPUT)
    private Long oid;
    private String content;
    private int status;
    private int limitTime;
    //下单时间
    private Timestamp publishDate;
    private String evaluate;
    //出单时间
    private int uid;
    private int ucid;
    private String address;
    private String phone;
    private int price;
    private String customer;
    private int cost;
    private int gid;
    private String geoname;
    private double distance;
    String gmtPayment;
    String aliPaytradeNo;
    @TableField(exist = false)
    private User user;

    @TableField(exist = false)
    private String pay;


}
