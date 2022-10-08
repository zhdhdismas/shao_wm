package com.shz.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.shz.dto.RespBean;
import com.shz.dto.Result;
import com.shz.entity.Menu;
import com.shz.entity.MenuDetail;
import com.shz.entity.UserRate;
import com.shz.mapper.MenuDetailMapper;
import com.shz.mapper.UserRateMapper;
import com.shz.service.MenuDetailService;
import com.shz.service.MenuService;
import com.shz.utils.UserUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.shz.utils.RedisConstants.CACHE_MENU_DETAIL_KEY;


@RestController
@RequestMapping("/menus")
public class MenuController {
    @Resource
    MenuService menuService;
    @Resource
    MenuDetailService menuDetailService;
    @Resource
    UserRateMapper userRateMapper;
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @PostMapping("/addOne")
    public RespBean addOneMenu(@RequestBody Menu menu) {

        if (menu.getTitle().length() == 0 || menu.getPrice() <= 0
                || menu.getCost() <= 0 || menu.getPrice() <= menu.getCost()
                || menu.getIntroduction().length() == 0 || menu.getLocation().length() == 0) {
            return new RespBean("error", "输入信息不得为空,输入信息必须合法");
        }
        if (menuService.titleExist(menu.getTitle())) {
            return new RespBean("error", "菜名已存在");
        }
        menu.setStatus(true);
        if (menuService.addOneMenu(menu)) {
            Menu m = menuService.query().eq("title", menu.getTitle()).one();
            MenuDetail md = new MenuDetail();
            md.setMid(m.getMid());
            md.setCount(0);
            md.setRate((double) 0);
            boolean save = menuDetailService.save(md);
            if (save) {
                return new RespBean("success", "加菜成功");
            } else {
                return new RespBean("error", "加菜失败");
            }
        }

        return new RespBean("error", "加菜失败");
    }

    @GetMapping("/all")
    public Map<String, Object> getMenusByState(@RequestParam(value = "state", defaultValue = "-1") Integer state, @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "count", defaultValue = "6") Integer count, String keywords) {
        Result res = menuService.getMenuByState(state, page, count, keywords);
        Map<String, Object> map = new HashMap<>();
        map.put("totalCount", res.getTotalCount());
        map.put("menus", res.getList());
        return map;
    }

    @GetMapping("/getAMenu/{mid}")
    public Menu getAMenu(@PathVariable Integer mid) {
        return menuService.getAMenu(mid);

    }

    @DeleteMapping("/dustbin")
    public RespBean deleteMenus(Integer[] mids) {
        if (menuService.deleteMenus(mids)) {
            boolean remove = menuDetailService.remove(new QueryWrapper<MenuDetail>().in("mid", Arrays.asList(mids)));
            if (remove) {
                return new RespBean("success", "删除成功!");
            } else {
                return new RespBean("error", "删除失败");
            }
        }
        return new RespBean("error", "删除失败!");
    }

    @DeleteMapping("/dustbinOne")
    public RespBean deleteThisMenu(Integer mid) {
        if (menuService.deleteThisMenu(mid)) {
            boolean remove = menuDetailService.remove(new QueryWrapper<MenuDetail>().eq("mid", mid));
            if (remove) {
                return new RespBean("success", "删除成功");
            } else {
                return new RespBean("error", "删除失败");
            }
        }
        return new RespBean("error", "删除失败!");

    }

    @PutMapping("/updateThisMenus")
    public RespBean updateMenu(Menu menu) {
        if (menu.getTitle().length() == 0 || menu.getPrice() <= 0
                || menu.getCost() <= 0 || menu.getPrice() < menu.getCost()
                || menu.getIntroduction().length() == 0 || menu.getLocation().length() == 0) {
            return new RespBean("error", "输入信息不得为空,输入信息必须合法");
        }

        if (menuService.updateMenu(menu)) {

            return new RespBean("success", "修改成功");

        }

        return new RespBean("error", "修改失败!");
    }

    @PostMapping("/rate")
    public RespBean toRate(Integer point, Integer mid) {
        MenuDetail one = menuDetailService.query().select("rate").eq("mid", mid).one();
        boolean update = menuDetailService
                .update(new UpdateWrapper<MenuDetail>()
                        .set("rate", point + one.getRate()).setSql("count=count+1").eq("mid", mid));
        int uid = UserUtil.getCurrentUser().getUid();
        UserRate userRate = new UserRate();
        userRate.setUid(uid);
        userRate.setMid(mid);
        userRate.setRate(point);
        userRateMapper.insert(userRate);
        if (update) {
            return new RespBean("success", "评价成功");
        } else {
            return new RespBean("error", "评价失败");
        }
    }

    @GetMapping("/isRate/{mid}")
    public Map<String, Object> isRate(@PathVariable Integer mid) {
        int uid = UserUtil.getCurrentUser().getUid();
        UserRate userRate = userRateMapper.selectOne(new QueryWrapper<UserRate>().eq("uid", uid).eq("mid", mid));
        HashMap<String, Object> map = new HashMap<>();
        map.put("isRate", userRate != null);
        if (userRate != null) {
            map.put("myRate", userRate.getRate());
        }
        return map;
    }

    @GetMapping("/average/{mid}")
    public double getAveragePoint(@PathVariable Integer mid) {
        MenuDetail md = menuDetailService.query().eq("mid", mid).one();
        if (md.getCount() != 0) {
            return md.getRate() / md.getCount();
        } else {
            return 0;
        }
    }

    @PutMapping("/updateDetail")
    public RespBean updateDetail(MenuDetail md) {
        boolean update = menuDetailService.update(new UpdateWrapper<MenuDetail>()
                .set("time", md.getTime()).set("way", md.getWay()).set("zhuliao", md.getZhuliao())
                .set("weidao", md.getWeidao()).eq("mid", md.getMid()));
        if (update) {
            Boolean delete = stringRedisTemplate.delete(CACHE_MENU_DETAIL_KEY + md.getMid());
            if (Boolean.TRUE.equals(delete)) {
                return new RespBean("success", "修改成功");
            } else {
                return new RespBean("error", "修改失败");
            }
        } else {
            return new RespBean("error", "修改失败");
        }
    }

    @GetMapping("/getAMenuDetail/{mid}")
    public MenuDetail getAMenuDetail(@PathVariable Integer mid) {

        return menuDetailService.getAMenuDetail(mid);
    }

    @PutMapping("/updateStatus")
    public RespBean updateStatus(Integer mid, Boolean status) {
        return menuService.updateStatus(mid, status);
    }

}
