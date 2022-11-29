package com.shz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.dto.RespBean;
import com.shz.dto.Result;
import com.shz.entity.Menu;

public interface MenuService extends IService<Menu> {
    boolean addOneMenu(Menu menu);

    Result getMenuByState(Integer state, Integer page, Integer count, String keywords);

    Menu getAMenu(Integer mid);

    boolean deleteMenus(Integer[] mids);

    boolean deleteThisMenu(Integer mid);

    boolean updateMenu(Menu menu);

    boolean titleExist(String title);

    void deleteAllCache();

    void deleteOneCache(int mid);

    boolean ThisCategoryMenusNone(String ids);


    RespBean updateStatus(Integer mid, Boolean status);

}
