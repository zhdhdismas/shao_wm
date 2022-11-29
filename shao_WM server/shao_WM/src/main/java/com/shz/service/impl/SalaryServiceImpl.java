package com.shz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.entity.Salary;
import com.shz.mapper.SalaryMapper;
import com.shz.service.SalaryService;
import org.springframework.stereotype.Service;

@Service
public class SalaryServiceImpl extends ServiceImpl<SalaryMapper, Salary> implements SalaryService {

}