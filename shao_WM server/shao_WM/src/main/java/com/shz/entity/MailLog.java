package com.shz.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class MailLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private String msgId;

    private Integer sid;

    private Integer status;

    private String routeKey;

    private String exchange;

    private Integer count;

    private LocalDateTime tryTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}