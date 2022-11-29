package com.shz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.dto.RespBean;
import com.shz.entity.Text;

import java.util.List;

public interface TextService extends IService<Text> {

    List<Text> getTheText();

    RespBean saveThisText(Text text);

    RespBean updateThisText(String createTime,String text);

    RespBean deleteThisText(String createTime);
}
