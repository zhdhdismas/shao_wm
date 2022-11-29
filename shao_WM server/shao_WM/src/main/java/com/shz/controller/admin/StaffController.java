package com.shz.controller.admin;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alipay.easysdk.kernel.util.JsonUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.shz.dto.RespBean;
import com.shz.dto.Result;
import com.shz.entity.*;
import com.shz.mapper.MailLogMapper;
import com.shz.service.LevelService;
import com.shz.service.PositionService;
import com.shz.service.StaffService;
import com.shz.service.UserService;
import com.shz.utils.MailConstants;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@RequestMapping("/admin/staff")
@RestController
public class StaffController {
    @Resource
    StaffService staffService;
    @Resource
    LevelService levelService;
    @Resource
    PositionService positionService;
    @Resource
    UserService userService;
    @Resource
    RabbitTemplate rabbitTemplate;
    @Resource
    MailLogMapper mailLogMapper;

    @PostMapping("/addOne")
    public RespBean addOneStaff(@RequestBody Staff staff) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        staff.setUpdateTime(timestamp);
        boolean update = userService.update(new UpdateWrapper<User>().eq("uid", staff.getUid()).set("staff", true));
        boolean save = staffService.save(staff);
        if (save && update) {
            Staff one = staffService.query().eq("id",staff.getId()).one();
            User u = userService.query().eq("uid", one.getUid()).one();
            one.setUsername(u.getUsername());
            one.setPhone(u.getPhone());
            one.setEmail(u.getEmail());
            Level level = levelService.query().eq("id", staff.getLid()).one();
            one.setLevelName(level.getLevelName());
            Position position = positionService.query().eq("id", staff.getPid()).one();
            one.setName(position.getName());

            String msgId = UUID.randomUUID().toString();
            MailLog mailLog = new MailLog();
            mailLog.setMsgId(msgId);
            mailLog.setSid(staff.getId());
            mailLog.setStatus(0);
            mailLog.setRouteKey(MailConstants.MAIL_ROUTING_KEY_NAME);
            mailLog.setExchange(MailConstants.MAIL_EXCHANGE_NAME);
            mailLog.setCount(0);
            mailLog.setTryTime(LocalDateTime.now().plusMinutes(MailConstants.MSG_TIMEOUT));
            mailLog.setCreateTime(LocalDateTime.now());
            mailLog.setUpdateTime(LocalDateTime.now());
            mailLogMapper.insert(mailLog);
            //发送信息
            rabbitTemplate.convertAndSend(MailConstants.MAIL_EXCHANGE_NAME,MailConstants.MAIL_ROUTING_KEY_NAME
                    ,JSONUtil.toJsonStr(one),new CorrelationData(msgId));


            rabbitTemplate.convertAndSend("mail.welcome", JSONUtil.toJsonStr(one));
            return new RespBean("success", "添加成功");
        } else {
            return new RespBean("error", "添加失败");
        }

    }

    @GetMapping("/all")
    public Map<String, Object> getStaffByState(String username, @RequestParam(value = "page", defaultValue = "1") Integer page, @RequestParam(value = "count", defaultValue = "6") Integer count) {
        int start = (page - 1) * count;
        Result res = staffService.getStaffByName(username, start, count);
        Map<String, Object> map = new HashMap<>();
        map.put("totalCount", res.getTotalCount());
        map.put("staff", res.getList());
        return map;
    }

    @GetMapping("/getAStaff/{id}")
    public Staff getAStaff(@PathVariable Integer id) {
        Staff staff = staffService.query().eq("id", id).one();
        Integer lid = staff.getLid();
        Level le = levelService.query().eq("lid", lid).one();
        Integer uid = staff.getUid();
        User user = userService.query().eq("uid", uid).one();
        staff.setUsername(user.getUsername());
        staff.setEmail(user.getEmail());
        staff.setPhone(user.getPhone());
        staff.setLevelName(le.getLevelName());
        return staff;

    }

    @DeleteMapping("/delete/{ids}/{uids}")
    public RespBean deleteAll(@PathVariable String ids, @PathVariable String uids) {
        String[] split = ids.split(",");
        List<String> list = Arrays.asList(split);
        boolean remove = staffService.remove(new QueryWrapper<Staff>().in("id", list));
        String[] split2 = uids.split(",");
        List<String> list2 = Arrays.asList(split2);
        boolean update = userService.update(new UpdateWrapper<User>().in("uid", list2).set("staff", false));
        if (remove && update) {
            return new RespBean("success", "删除成功");
        } else {
            return new RespBean("error", "删除失败");
        }
    }


    @PutMapping("/updateThisStaff")
    public RespBean updateStaff(Staff staff) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        staff.setUpdateTime(timestamp);
        boolean update = staffService.updateById(staff);
        if (update) {
            return new RespBean("success", "修改成功");
        } else {
            return new RespBean("error", "修改失败");
        }
    }

    @GetMapping(value = "/export", produces = "application/octet-stream")
    public void exportEmployee(HttpServletResponse response) {
        List<Staff> list = (List<Staff>) staffService.getStaffByName("", 1, 1000).getList();
        ExportParams params = new ExportParams("员工表", "员工表", ExcelType.HSSF);
        Workbook workbook = ExcelExportUtil.exportExcel(params, Staff.class, list);
        ServletOutputStream outputStream = null;
        try {
            //流形式
            response.setHeader("content-type", "application/octet-stream");
            //中文乱码
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode("员工表.xls", "UTF-8"));
            outputStream = response.getOutputStream();
            workbook.write(outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != outputStream) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @PostMapping("/import")
    public RespBean importEmployee(MultipartFile file) {
        ImportParams params = new ImportParams();
        //去掉标题行
        params.setTitleRows(1);
        List<Position> positions = positionService.query().list();
        List<Level> levels = levelService.query().list();
        List<User> users = userService.query().list();
        try {
            List<Staff> list = ExcelImportUtil.importExcel(file.getInputStream(), Staff.class, params);
            list.forEach(staff -> {
                staff.setUid(users.get(users.indexOf(new User(staff.getUsername(),staff.getPhone(),staff.getEmail()))).getUid());
                staff.setLid(levels.get(levels.indexOf(new Level(staff.getLevelName()))).getId());
                staff.setPid(positions.get(positions.indexOf(new Position(staff.getName()))).getId());
            });
            if (staffService.saveBatch(list)) {
                return new RespBean("success", "导入成功");
            }
            return new RespBean("error", "导入失败");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new RespBean("error","导入失败");
    }

}
