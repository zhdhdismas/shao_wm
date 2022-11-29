package com.shz.controller;

import com.shz.dto.RespBean;
import com.shz.dto.Result;
import com.shz.dto.ScrollResult;
import com.shz.dto.UserDTO;
import com.shz.entity.Blog;
import com.shz.entity.User;
import com.shz.service.BlogService;
import com.shz.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/blog")
public class BlogController {
    @Resource
    BlogService blogService;
    @Resource
    UserService userService;
    @GetMapping("/all")
    public Map<String, Object> getBlogsByState( @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "count", defaultValue = "6") Integer count, String keywords) {
        Result res = blogService.getBlogByState( page, count, keywords);
        Map<String, Object> map = new HashMap<>();
        map.put("totalCount", res.getTotalCount());
        map.put("blogs", res.getList());
        return map;
    }
    @GetMapping("/getMyBlogs")
    public Map<String, Object> getMyBlogs( @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "count", defaultValue = "6") Integer count, String keywords) {
        Result res = blogService.getMyBlogs( page, count, keywords);
        Map<String, Object> map = new HashMap<>();
        map.put("totalCount", res.getTotalCount());
        map.put("blogs", res.getList());
        return map;
    }

    @GetMapping("/getABlog/{bid}")
    public Blog getABlog(@PathVariable Long bid) {
        return blogService.getABlog(bid);
    }

    @DeleteMapping("/dustbin")
    public RespBean deleteBlogs(Long[] bids) {
        if (blogService.deleteBlogs(bids)) {
            return new RespBean("success", "删除成功!");
        }
        return new RespBean("error", "删除失败!");
    }

    @DeleteMapping("/dustbinOne")
    public RespBean deleteThisBlog(Long bid) {
        if (blogService.deleteThisBlog(bid)) {
            return new RespBean("success", "删除成功");
        }
        return new RespBean("error", "删除失败!");

    }

    @PutMapping("/updateBlog")
    public RespBean updateBlog(Blog blog) {
        if (blogService.updateBlog(blog)) {
            return new RespBean("success", "修改成功");
        }
        return new RespBean("error", "修改失败!");
    }
    @PostMapping("/saveBlog")
    public RespBean saveBlog(@RequestBody Blog blog) {
        if (blogService.saveThisBlog(blog)) {
            return new RespBean("success", "发表成功");
        }
        return new RespBean("error", "博客名已存在,发表失败!");
    }
    @GetMapping("/getBlogUser")
    public User getBlogUser(Integer uid){
        User u = userService.query().eq("uid", uid).one();
        return u;
    }
    @PostMapping("/likeBlog")
    public RespBean likeThisBlog(Long bid){
        return blogService.likeThisBlog(bid);
    }
    @GetMapping("/blogLikes/{bid}")
    public List<UserDTO> blogLikes(@PathVariable("bid")  Long bid){
        return  blogService.queryBlogLikes(bid);
    }
    @GetMapping("/of/follow")
    public ScrollResult queryBlogOfFollow(
            @RequestParam("lastId")Long max,@RequestParam(value = "offset",defaultValue = "0")Integer offset){
        return blogService.queryBlogOfFollow ( max,offset );
    }
}
