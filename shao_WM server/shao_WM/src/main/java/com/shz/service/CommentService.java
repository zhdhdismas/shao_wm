package com.shz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.dto.Result;
import com.shz.entity.Comment;

public interface CommentService extends IService<Comment> {
    Result getMenuComment(Integer id, Integer page, Integer count);

    boolean addMenuComment(Comment comment);

    boolean deleteMyThisComment(Integer cid, Integer uid, Integer mid);
}
