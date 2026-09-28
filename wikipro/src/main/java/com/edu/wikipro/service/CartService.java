package com.edu.wikipro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.wikipro.common.Result;
import com.edu.wikipro.entity.Cart;

import java.util.List;

public interface CartService extends IService<Cart> {
    Result<?> addCart(Integer userId, Integer goodsId, Integer quantity);
    Result<?> findCartByUserId(Integer userId);
    Result<?> updateQuantity(Integer cartId, Integer quantity);
    Result<?> deleteCart(Integer cartId);
    Result<?> batchDeleteCart(List<Integer> cartIds);
    Result<?> getCartCount(Integer userId);
}