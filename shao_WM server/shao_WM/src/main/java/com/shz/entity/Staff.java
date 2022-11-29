package com.shz.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class Staff implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private Integer pid;
    private Integer lid;
    private String description;
    @TableField(exist = false)
    @Excel(name = "员工姓名")
    private String username;
    @TableField(exist = false)
    @Excel(name = "电话", width = 15)
    private String phone;
    @TableField(exist = false)
    @Excel(name = "邮箱", width = 30)
    private String email;
    @Excel(name = "性别")
    private String gender;
    @Excel(name = "年龄")
    private Integer age;
    @Excel(name = "联系地址", width = 40)
    private String address;
    @Excel(name = "学历")
    private String education;
    @Excel(name = "毕业院校")
    private String college;
    @Excel(name = "专业")
    private String major;
    @Excel(name = "职位")
    @TableField(exist = false)
    private String name;
    @TableField(exist = false)
    @Excel(name = "等级")
    private String levelName;
    @Excel(name = "部门")
    private String department;
    @Excel(name = "工资账套")
    private String salary;
    @Excel(name = "上次修改时间", format = "yyyy-MM-dd", width = 20)
    private Timestamp updateTime;

}
