package com.shz.entity;

import lombok.Data;

@Data
public class AliPay {
    private String subject;
    private String traceNo;
    private String totalAmount;
}