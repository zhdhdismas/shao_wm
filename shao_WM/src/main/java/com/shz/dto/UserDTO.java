package com.shz.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String phone;
    private Integer uid;
}
