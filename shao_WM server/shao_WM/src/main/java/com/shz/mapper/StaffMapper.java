package com.shz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shz.dto.Result;
import com.shz.entity.Staff;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StaffMapper extends BaseMapper<Staff> {
    List<Staff> getStaffByName(@Param("username")String username, @Param("start")int start, @Param("count")Integer count);

    Integer getStaffByNameTotalCount(@Param("username") String username, @Param("start") int start, @Param("count") Integer count);
}
