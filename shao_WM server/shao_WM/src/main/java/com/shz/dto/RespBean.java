package com.shz.dto;

import java.util.HashMap;

/**
 * Created by sang on 2017/12/17.
 */
public class RespBean {
    private String status;
    private String msg;
    private Object object;

    public RespBean(String status, String msg) {

        this.status = status;
        this.msg = msg;
    }

    public RespBean(String status, Object object) {
        this.status=status;
        this.object=object;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
