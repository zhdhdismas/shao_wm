package com.shz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.dto.Result;
import com.shz.entity.Staff;

public interface StaffService extends IService<Staff> {
    Result getStaffByName(String username, int start, Integer count);

    boolean ThisStaffsLidNone(String ids);

    boolean ThisStaffsPidNone(String ids);
}
