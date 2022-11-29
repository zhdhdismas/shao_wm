package com.shz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class Follow implements Serializable {
    @TableId(type = IdType.AUTO)
    private int fid;
    private int uid;
    private int fuid;
}
