package com.shz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shz.entity.Department;

import java.util.List;

public interface DepartmentMapper extends BaseMapper<Department> {

    List<Department> getAllDepartmentsByParentId(Integer parentId);



}