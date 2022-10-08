package com.shz.controller;

import cn.hutool.core.lang.Pair;
import com.shz.dto.JwtProperties;
import com.shz.dto.RespBean;
import com.shz.dto.Result;
import com.shz.entity.Address;
import com.shz.entity.Comment;
import com.shz.entity.Text;
import com.shz.entity.User;
import com.shz.service.*;
import com.shz.utils.JwtTokenUtil;
import com.shz.utils.OSSUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import com.shz.utils.UserUtil;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Resource
    UserService userService;
    @Resource
    CommentService commentService;
    @Resource
    AddressService addressService;
    @Resource
    OrderService orderService;

    @Resource
    private TextService textService;

    @GetMapping("/currentUserName")
    public RespBean currentUserName() {
        if (UserUtil.getCurrentUser()==null) {
            return new RespBean("error","请先登录");
        }else{
            return new RespBean("success",UserUtil.getCurrentUser().getUsername());
        }

    }

    @GetMapping("/currentUserId")
    public Integer currentUserId() {
        return UserUtil.getCurrentUser().getUid();
    }
    @GetMapping("/currentUserRole")
    public Integer currentUserRole(){
        return  UserUtil.getCurrentUser().getRoleName();
    }

    @GetMapping("/currentUserMsg")
    public User currentUserMsg() {
        //所有地址在对话框中
        List<Address> address = userService.getCurrentUserAddress();

        return userService.getResultUser(address);
    }
    @GetMapping("/isExistPassword")
    public boolean isExistPassword(){
        String password = UserUtil.getCurrentUser().getPassword();
        return password!=null&&password.length()!=0;
    }


    @PostMapping("/addOneAddress")
    public RespBean addOneUserAddress(Address address) {
        String address1 = address.getAddress();
        Integer uid = UserUtil.getCurrentUser().getUid();
        if (address1.length() == 0) return new RespBean("success", "修改成功");
        if (addressService.selAddressExist(address1, uid)) {
            return new RespBean("success", "修改成功");
        }

        boolean isSuccess = addressService.addOneAddress(address1);
        if (isSuccess) {
            return new RespBean("success", "修改成功");
        } else {
            return new RespBean("error", "修改失败");
        }

    }


    @RequestMapping("/isAdmin")
    public Boolean isAdmin() {
        List<GrantedAuthority> authorities = UserUtil.getCurrentUser().getAuthorities();

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().contains("2")) {
                return true;
            }
        }
        return false;
    }

    @RequestMapping("/isRider")
    public Boolean isRider() {
        List<GrantedAuthority> authorities = UserUtil.getCurrentUser().getAuthorities();

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().contains("1")) {
                return true;
            }
        }
        return false;
    }

    @PutMapping("/updateMsg")
    public RespBean updateMsg(User user, String curpassword) {
        if (isRider() && orderService.getMyCurReceiveOrder(1, 1).getTotalCount() != 0) {
            return new RespBean("error", "还有未完成的订单,请先完成订单");
        }
        String pass = UserUtil.getCurrentUser().getPassword();
        if (pass!=null&&pass.length()!=0&&userService.compareTwoPassword(user.getPassword())) {
            return new RespBean("error", "原密码错误");
        }
        if (userService.compareUserName(user.getUsername())) {
            if (userService.loadUserByUsername(user.getUsername()) != null) {
                return new RespBean("error", "用户名已存在");
            }
        }
        if(userService.compareUserEmail(user.getEmail())){
            if(userService.query().eq("email",user.getEmail()).one()!=null){
                return new RespBean("error", "邮箱已被其他用户绑定");
            }
        }
        if (userService.updateUserMsg(user, curpassword)) {
            return new RespBean("success", "修改成功!");
        }
        return new RespBean("error", "修改失败!");
    }

    @GetMapping("/getComments/{id}")
    public Map<String, Object> getArticleComment(@PathVariable Integer id, @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "count", defaultValue = "6") Integer count) {
        Map<String, Object> map = new HashMap<>();
        Result result = commentService.getMenuComment(id, page, count);
        map.put("comments", result.getList());
        map.put("totalCount", result.getTotalCount());
        return map;
    }

    @PostMapping("/addComment")
    public RespBean addArticleComment(Comment comment) {
        boolean res = commentService.addMenuComment(comment);
        if (res) {
            return new RespBean("success", "添加成功");
        } else {
            return new RespBean("error", "添加失败");
        }
    }

    @DeleteMapping("/deleteMyThisComment")
    public RespBean deleteMyThisComment(Integer cid, Integer uid, Integer mid) {
        boolean res = commentService.deleteMyThisComment(cid, uid, mid);
        if (res) return new RespBean("success", "撤回成功");
        else return new RespBean("error", "撤回失败");
    }

    @PutMapping("/addAvatar")
    public String addAvatar(String avatar) {
        return userService.addAvatar(avatar);
    }

    @GetMapping("/myAvatar")
    public String getMyAvatar() {
        int uid = UserUtil.getCurrentUser().getUid();
        String img = userService.query().eq("uid", uid).one().getAvatar();
        String scaleAfter = "http://" + OSSUtil.SUFFER_URL + img;
        return scaleAfter;
    }

    @PostMapping("/sign")
    public RespBean sign() {
        return userService.sign();
    }

    @GetMapping("/sign/count")
    public Integer signCount() {
        return userService.signCount();
    }

    @GetMapping("/getSign")
    public Pair<List<String>, List<Text>> getMonthSign() {
        List<String> signList = userService.getSignList();
        List<Text> theText = textService.getTheText();
        return new Pair<>(signList, theText);
    }

    @PostMapping("/text")
    public RespBean addText(Text text) {
        return textService.saveThisText(text);
    }

    @PutMapping("/updateText")
    public RespBean updateText(String createTime, String text) {
        return textService.updateThisText(createTime, text);
    }

    @DeleteMapping("/deleteText")
    public RespBean deleteText(String createTime) {
        return textService.deleteThisText(createTime);
    }


}
