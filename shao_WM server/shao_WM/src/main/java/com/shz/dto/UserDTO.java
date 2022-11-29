package com.shz.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserDTO {
    private String username;
    private String email;
    private String avatar;
    private String phone;
    private Integer uid;
    private boolean enabled;
    private boolean staff;
    private Timestamp regTime;
}
