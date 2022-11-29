package com.shz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

import java.sql.Timestamp;


@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(of = "levelName")
public class Level {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @NonNull
    private String levelName;
    private Timestamp createTime;
}
