package com.shz.controller.admin;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.shz.dto.RespBean;
import com.shz.dto.UserDTO;
import com.shz.entity.Staff;
import com.shz.entity.User;
import com.shz.service.StaffService;
import com.shz.service.UserService;
import com.shz.utils.UserUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/user")
public class UserManaController {
    @Resource
    UserService userService;
    @Resource
    StaffService staffService;
    @GetMapping("/all")
    public List<UserDTO> userList(String username){
        List<User> list = userService.query().like("username",username).list();
        return UserUtil.toUserDTOs(list);
    }
    @DeleteMapping("/{uid}")
    public RespBean deleteOneUser(@PathVariable Integer uid){
        Staff sf = staffService.query().eq("uid", uid).one();
        if(sf!=null){
            return new RespBean("error","请在员工管理下先删除该员工");
        }
        boolean remove = userService.removeById(uid);
        if(remove){
            return new RespBean("success","删除成功");
        }else{
            return new RespBean("error","删除失败");
        }
    }
    @GetMapping("/{uid}")
    public UserDTO getOneUser(@PathVariable Integer uid){
        User user = userService.query().eq("uid", uid).one();
        return UserUtil.toUserDTO(user);
    }
    @PutMapping("/enabled")
    public RespBean updateUserEnabled(Boolean enabled,Integer uid){
        boolean update = userService.update(new UpdateWrapper<User>().set("enabled", enabled).eq("uid", uid));
        if(update){
            return new RespBean("success","修改成功");
        }else{
            return new RespBean("error","修改失败");
        }
    }
}
