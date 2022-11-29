package com.shz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shz.dto.Result;
import com.shz.dto.UserDTO;
import com.shz.entity.Comment;
import com.shz.entity.User;
import com.shz.mapper.CommentMapper;
import com.shz.service.CommentService;
import com.shz.service.UserService;
import org.springframework.stereotype.Service;
import com.shz.utils.UserUtil;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Resource
    UserService userService;


    @Override
    public Result getMenuComment(Integer id, Integer page, Integer count) {
        int start = (page - 1) * count;
        Page<Comment> pages = query().eq("mid", id).orderByDesc("publishDate").page(new Page<>(page, count));
        List<Comment> records = pages.getRecords();
        for (Comment comment : records) {
            User user=userService.getAUserOrUserCache(comment.getUid());
            UserDTO udo = UserUtil.toUserDTO(user);
            comment.setUserDTO(udo);
        }
        return new Result(records, (int)pages.getTotal());

    }

    @Override
    public boolean addMenuComment(Comment comment) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        comment.setPublishDate(timestamp);
        comment.setUid(UserUtil.getCurrentUser().getUid());
        return save(comment);
    }

    @Override
    public boolean deleteMyThisComment(Integer cid, Integer uid, Integer mid) {
        return remove(new QueryWrapper<Comment>().eq("cid", cid).eq("uid", uid).eq("mid", mid));
    }
}
