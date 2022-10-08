package com.shz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Follow {
    @TableId(type = IdType.AUTO)
    private int fid;
    private int uid;
    private int fuid;
}
