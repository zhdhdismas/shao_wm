package com.shz.dto;

import java.util.List;

public class Result {
    List<?> list;
    Integer totalCount;

    public Result(List<?> list, Integer totalCount) {
        this.list = list;
        this.totalCount = totalCount;
    }

    public List<?> getList() {
        return list;
    }

    public void setList(List<?> list) {
        this.list = list;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
