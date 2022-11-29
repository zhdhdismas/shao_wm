package com.shz.dto;

import com.shz.entity.User;

import java.util.List;

public class Bill {
    private List<User> userList;
    private int totalCount;
    private int sumPrice;
    private int profit;
    public Bill(){
    }
    public Bill(List<User> userList, int totalCount, int sumPrice, int profit) {
        this.userList = userList;
        this.totalCount = totalCount;
        this.sumPrice = sumPrice;
        this.profit = profit;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(int sumPrice) {
        this.sumPrice = sumPrice;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }
}
