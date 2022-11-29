package com.shz.controller.admin;

import com.shz.dto.RespBean;
import com.shz.entity.Category;
import com.shz.entity.Level;
import com.shz.service.LevelService;
import com.shz.service.MenuService;
import com.shz.service.StaffService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/level")
public class LevelController {
    @Resource
    LevelService levelService;

    @Resource
    StaffService staffService;

    @GetMapping("/all")
    public List<Level> getAllLevel(){
        return levelService.getAll();
    }
    @PutMapping("/update")
    public RespBean updateName(int id, String name){
        if(levelService.updateIdAndName(id,name)){
            return new RespBean("success","修改成功");
        }else{
            return new RespBean("error","修改失败");
        }
    }
    @PostMapping("/add")
    public RespBean addCate(String name){
        if(levelService.query().eq("levelName",name).one()!=null){
            return new RespBean("error","等级名已存在,添加失败");
        }
        if(levelService.addCate(name)){
            return new RespBean("success","添加成功");
        }else{
            return new RespBean("error","添加失败");
        }
    }
    @DeleteMapping("/delete/{ids}")
    public RespBean deleteAll(@PathVariable String ids){
        if(!staffService.ThisStaffsLidNone(ids)){
            return new RespBean("error","请先删除该等级下所有员工");
        }
        if(levelService.deleteAll(ids)){
            return new RespBean("success","删除成功");
        }else{
            return new RespBean("error","删除失败");
        }
    }
}
