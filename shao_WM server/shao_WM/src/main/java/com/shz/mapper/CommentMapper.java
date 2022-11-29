package com.shz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shz.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {




}
