package com.edu.wikipro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.wikipro.common.Result;
import com.edu.wikipro.entity.Orders;

import java.util.List;
import java.util.Map;

public interface OrdersService extends IService<Orders> {
    Result<?> addOrder(Integer userId, Double totalPrice, List<Map<String, Object>> cartList, String address);
    Result<?> findOrderByOrderId(Integer orderId, Integer userId);
    Result<?> findItemByOrderId(Integer orderId, Integer userId);
    Result<?> cancelOrder(Integer orderId, Integer userId);
    Result<?> cancelBatch(List<Integer> orderIds, Integer userId);
    Result<?> findOrdersByUserId(Integer userId);
}