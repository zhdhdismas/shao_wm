
package com.shz.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.entity.Department;
import com.shz.mapper.DepartmentMapper;
import com.shz.service.DepartmentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {

    @Resource
    private DepartmentMapper departmentMapper;

    @Override
    public List<Department> getAllDepartments() {

        return departmentMapper.getAllDepartmentsByParentId(-1);
    }

}