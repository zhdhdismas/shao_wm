package com.shz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.entity.Level;
import com.shz.entity.Position;

import java.util.List;

public interface PositionService extends IService<Position> {
    List<Position> getAll();

    boolean updateIdAndName(int id, String name);
    void deleteAllLevelCache();

    boolean addCate(String name);

    boolean deleteAll(String ids);
}
