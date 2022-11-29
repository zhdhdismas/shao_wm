package com.shz.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MailLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private String msgId;

    private Integer eid;

    private Integer status;

    private String routeKey;

    private String exchange;

    private Integer count;

    private LocalDateTime tryTime;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Integer getEid() {
        return eid;
    }

    public void setEid(Integer eid) {
        this.eid = eid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRouteKey() {
        return routeKey;
    }

    public void setRouteKey(String routeKey) {
        this.routeKey = routeKey;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public LocalDateTime getTryTime() {
        return tryTime;
    }

    public void setTryTime(LocalDateTime tryTime) {
        this.tryTime = tryTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}