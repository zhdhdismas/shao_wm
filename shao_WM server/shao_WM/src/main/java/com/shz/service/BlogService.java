package com.shz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.dto.RespBean;
import com.shz.dto.Result;
import com.shz.dto.ScrollResult;
import com.shz.dto.UserDTO;
import com.shz.entity.Blog;

import java.util.List;

public interface BlogService extends IService<Blog> {


    Result getBlogByState(Integer page, Integer count, String keywords);

    Blog getABlog(Long bid);

    boolean deleteBlogs(Long[] bids);

    void deleteAllCache(int uid);

    void deleteOneCache(Long bid);

    boolean deleteThisBlog(Long bid);


    Result getMyBlogs(Integer page, Integer count, String keywords);

    boolean saveThisBlog(Blog blog);

    boolean updateBlog(Blog blog);

    RespBean likeThisBlog(Long bid);

    List<UserDTO> queryBlogLikes(Long bid);

    ScrollResult queryBlogOfFollow(Long max, Integer offset);
}
