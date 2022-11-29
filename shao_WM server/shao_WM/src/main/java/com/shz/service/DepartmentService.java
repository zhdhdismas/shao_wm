package com.shz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.entity.Department;

import java.util.List;


public interface DepartmentService extends IService<Department> {


    List<Department> getAllDepartments();


}