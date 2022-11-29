package com.shz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.entity.MenuDetail;

public interface MenuDetailService extends IService<MenuDetail> {
    MenuDetail getAMenuDetail(Integer mid);
}
