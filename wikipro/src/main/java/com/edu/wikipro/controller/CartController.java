package com.edu.wikipro.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.wikipro.common.Result;
import com.edu.wikipro.entity.WxUser;
import com.edu.wikipro.mapper.WxUserMapper;
import com.edu.wikipro.service.CartService;
import com.edu.wikipro.utils.JWTUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CartController {

    @Resource
    private CartService cartService;
    
    @Resource
    private WxUserMapper wxUserMapper;

    private Integer getUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            return null;
        }
        token = token.replace("Bearer ", "").trim();
        String username = JWTUtils.getUsername(token);
        if (username == null) {
            return null;
        }
        QueryWrapper<WxUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        WxUser user = wxUserMapper.selectOne(wrapper);
        return user != null ? user.getUserId() : null;
    }

    @PostMapping("/add")
    public Result<?> add(@RequestBody java.util.Map<String, Integer> params, HttpServletRequest request) {
        Integer goodsId = params.get("goodsId");
        Integer quantity = params.get("quantity");
        if (goodsId == null || quantity == null) {
            return Result.fail("参数错误");
        }

        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("未登录");
        }

        return cartService.addCart(userId, goodsId, quantity);
    }

    @GetMapping("/list")
    public Result<?> list(HttpServletRequest request) {
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("未登录");
        }

        return cartService.findCartByUserId(userId);
    }

    @PostMapping("/update")
    public Result<?> update(@RequestBody java.util.Map<String, Integer> params, HttpServletRequest request) {
        Integer cartId = params.get("cartId");
        Integer quantity = params.get("quantity");
        if (cartId == null || quantity == null) {
            return Result.fail("参数错误");
        }

        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("未登录");
        }

        return cartService.updateQuantity(cartId, quantity);
    }

    @PostMapping("/delete")
    public Result<?> delete(@RequestBody java.util.Map<String, Integer> params, HttpServletRequest request) {
        Integer cartId = params.get("cartId");
        if (cartId == null) {
            return Result.fail("参数错误");
        }

        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("未登录");
        }

        return cartService.deleteCart(cartId);
    }

    @PostMapping("/batchDelete")
    public Result<?> batchDelete(@RequestBody java.util.List<Integer> cartIds, HttpServletRequest request) {
        if (cartIds == null || cartIds.isEmpty()) {
            return Result.fail("参数错误");
        }

        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("未登录");
        }

        return cartService.batchDeleteCart(cartIds);
    }

    @GetMapping("/count")
    public Result<?> count(HttpServletRequest request) {
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("未登录");
        }

        return cartService.getCartCount(userId);
    }
}