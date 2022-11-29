package com.shz.controller.admin;

import com.shz.dto.RespBean;
import com.shz.entity.Level;
import com.shz.entity.Position;
import com.shz.service.LevelService;
import com.shz.service.PositionService;
import com.shz.service.StaffService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/position")
public class PositionController {
    @Resource
    PositionService positionService;

    @Resource
    StaffService staffService;

    @GetMapping("/all")
    public List<Position> getAllLevel(){
        return positionService.getAll();
    }
    @PutMapping("/update")
    public RespBean updateName(int id, String name){
        if(positionService.updateIdAndName(id,name)){
            return new RespBean("success","修改成功");
        }else{
            return new RespBean("error","修改失败");
        }
    }
    @PostMapping("/add")
    public RespBean addCate(String name){
        if(positionService.query().eq("name",name).one()!=null){
            return new RespBean("error","等级名已存在,添加失败");
        }
        if(positionService.addCate(name)){
            return new RespBean("success","添加成功");
        }else{
            return new RespBean("error","添加失败");
        }
    }
    @DeleteMapping("/delete/{ids}")
    public RespBean deleteAll(@PathVariable String ids){
        if(!staffService.ThisStaffsPidNone(ids)){
            return new RespBean("error","请先删除该职位下所有员工");
        }
        if(positionService.deleteAll(ids)){
            return new RespBean("success","删除成功");
        }else{
            return new RespBean("error","删除失败");
        }
    }
}
