package com.shz.controller.admin;

import com.shz.dto.RespBean;
import com.shz.entity.Category;
import com.shz.service.CategoryService;
import com.shz.service.MenuService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/category")
public class CategoryController {
    @Resource
    CategoryService categoryService;
    @Resource
    MenuService menuService;

    @GetMapping("/all")
    public List<Category> getAllCategory(){
        return categoryService.getAll();
    }
    @PutMapping("/update")
    public RespBean updateName(int id,String name){
        if(categoryService.updateIdAndName(id,name)){
            return new RespBean("success","修改成功");
        }else{
            return new RespBean("error","修改失败");
        }
    }
    @PostMapping("/add")
    public RespBean addCate(String name){
        if(categoryService.addCate(name)){
            return new RespBean("success","添加成功");
        }else{
            return new RespBean("error","添加失败");
        }
    }
    @DeleteMapping("/delete/{ids}")
    public RespBean deleteAll(@PathVariable String ids){
        if(!menuService.ThisCategoryMenusNone(ids)){
            return new RespBean("error","请先删除该分类下的所有菜");
        }
        if(categoryService.deleteAll(ids)){
            return new RespBean("success","删除成功");
        }else{
            return new RespBean("error","删除失败");
        }
    }
}
