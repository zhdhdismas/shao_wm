package com.shz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.dto.Result;
import com.shz.entity.Staff;
import com.shz.mapper.StaffMapper;
import com.shz.service.StaffService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements StaffService {
    @Resource
    StaffMapper staffMapper;

    @Override
    public Result getStaffByName(String username, int start, Integer count) {
        Integer total = staffMapper.getStaffByNameTotalCount(username, start, count);
        List<Staff> staffByName = staffMapper.getStaffByName(username, start, count);
        return new Result(staffByName, total);
    }

    @Override
    public boolean ThisStaffsLidNone(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        Integer count = query().in("lid", list).count();
        if (count > 0) {
            return false;
        }
        return true;
    }

    @Override
    public boolean ThisStaffsPidNone(String ids) {
        List<String> list = Arrays.asList(ids.split(","));
        Integer count = query().in("pid", list).count();
        if (count > 0) {
            return false;
        }
        return true;
    }
}
