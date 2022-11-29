package com.shz.controller.admin;



import com.shz.dto.RespBean;
import com.shz.entity.Salary;
import com.shz.service.SalaryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("admin/salary")
public class SalaryController {

    @Resource
    private SalaryService salaryService;

    @GetMapping("/all")
    public List<Salary> getAllSalary(){
        return salaryService.list();
    }

    @PostMapping("/add")
    public RespBean addSalary(@RequestBody Salary salary){
        salary.setCreateDate(LocalDateTime.now());
        if (salaryService.save(salary)){
            return new RespBean("success","添加成功");
        }
        return new RespBean("error","添加失败");
    }

    @DeleteMapping("delete/{id}")
    public RespBean deleteSalary(@PathVariable Integer id){
        if (salaryService.removeById(id)){
            return new RespBean("success","删除成功");

        }
        return new RespBean("error","删除失败");
    }

    @PutMapping("/update")
    public RespBean updateSalary(Salary salary){
        if (salaryService.updateById(salary)){
            return new RespBean("success","更新成功");
        }
        return new RespBean("error","更新失败");
    }

}