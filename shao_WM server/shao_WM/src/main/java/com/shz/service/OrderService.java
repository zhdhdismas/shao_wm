package com.shz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shz.dto.Result;
import com.shz.entity.Order;

import java.util.List;

public interface OrderService extends IService<Order> {
    int selectSumPrice(String startDate, String endDate,Integer gid);

    int selectSumCost(String startDate, String endDate,Integer gid);

    Result getAllOrderNone(Integer page, Integer count);

    boolean updateStateOrder(Long oid);

    boolean confirmOrder(Long oid);

    Result getMyCurReceiveOrder(Integer page, Integer count);

    Result getMyReceiveOrderAchieved(Integer page, Integer count);

    Result getAllOrder(Integer page, Integer count,String keyword,Integer status);

    List<Order> getMyCurOrder();

    Result getMyOrder(Integer page, Integer count);

    boolean updateState(Long oid, int uid);

    void addOnOrderToRedis(Order order);
}
