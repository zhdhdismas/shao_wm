package com.shz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import javax.annotation.Resource;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "name")
public class Position {
    @TableId(type=IdType.AUTO)
    private Integer id;
    @NonNull
    private String name;
    private Timestamp createTime;
}
