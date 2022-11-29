package com.shz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.entity.Level;

import java.util.List;

public interface LevelService extends IService<Level> {
    List<Level> getAll();

    boolean updateIdAndName(int id, String name);

    void deleteAllLevelCache();

    boolean addCate(String name);

    boolean deleteAll(String ids);
}
