package com.shz.service;

import com.shz.dto.Result;

public interface MoneyService {
    int getSumPrice(String startDate, String endDate,Integer gid);

    int getSumCost(String startDate, String endDate,Integer gid);

    Result getRiderUser(String startDate, String endDate, Integer page, Integer count);
}
