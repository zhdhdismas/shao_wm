package com.shz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<Category> getAll();

    boolean updateIdAndName(int id, String name);

    boolean addCate(String name);

    boolean deleteAll(String ids);

    void deleteAllCategoryCache();
}
