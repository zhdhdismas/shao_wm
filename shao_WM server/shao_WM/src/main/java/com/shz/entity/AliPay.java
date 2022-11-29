package com.shz.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class AliPay implements Serializable {
    private String subject;
    private String traceNo;
    private String totalAmount;
}