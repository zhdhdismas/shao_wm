package com.shz.controller.admin;



import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.shz.dto.RespBean;
import com.shz.entity.Department;
import com.shz.service.DepartmentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *

 */
@RestController
@RequestMapping("/admin/department")
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    @GetMapping("/all")
    public List<Department> getAllDepartments(){
        return departmentService.getAllDepartments();
    }
    @GetMapping("/allChild")
    public List<Department> getChildDepartment(){
        return departmentService.query().eq("isParent",false).list();
    }

    @PostMapping("/add")
    public Map<String,Object> addDep(Department department){
        Integer parentId = department.getParentId();
        departmentService.update(new UpdateWrapper<Department>().set("isParent",true).eq("id",parentId));
        department.setIsParent(false);
        boolean save = departmentService.save(department);
        Department dep = departmentService.query().eq("id", department.getId()).one();
        dep.setChildren(Collections.emptyList());
        Map<String,Object> map=new HashMap<>();
        map.put("msg","添加成功");
        map.put("obj",dep);
        return map;
    }

    @DeleteMapping("/{id}")
    public RespBean deleteDep(@PathVariable Integer id){
        Department dep = departmentService.query().eq("id", id).one();
        Integer parentId = dep.getParentId();
        if (departmentService.query().eq("parentId",parentId).count()==1) {
            departmentService.update(new UpdateWrapper<Department>().eq("id",parentId).set("isParent",false));
        }
        boolean remove = departmentService.removeById(id);
        return new RespBean("success","删除成功");
    }


}