package com.shz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class VoucherOrder implements Serializable {
    @TableId(type=IdType.INPUT)
    private Long vcid;
    private Integer uid;
    private Long vid;
}
