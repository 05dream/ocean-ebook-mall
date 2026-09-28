package com.edu.wikipro.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.wikipro.common.Result;
import com.edu.wikipro.entity.Orders;
import com.edu.wikipro.entity.WxUser;
import com.edu.wikipro.mapper.OrdersMapper;
import com.edu.wikipro.mapper.WxUserMapper;
import com.edu.wikipro.service.OrdersService;
import com.edu.wikipro.utils.JWTUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    @Resource
    private OrdersService ordersService;
    
    @Resource
    private WxUserMapper wxUserMapper;
    
    @Resource
    private AlipayClient alipayClient;
    
    @Resource
    private OrdersMapper ordersMapper;

    private final Gson gson = new Gson();

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
    public Result<?> add(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        String cartListJson = (String) params.get("cartListJson");
        Double totalPrice = params.get("totalPrice") != null ? ((Number) params.get("totalPrice")).doubleValue() : 0.0;
        String address = (String) params.get("address");

        if (cartListJson == null || cartListJson.isEmpty()) {
            return Result.fail("参数错误");
        }

        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("未登录");
        }

        List<Map<String, Object>> cartList = gson.fromJson(cartListJson, new TypeToken<List<Map<String, Object>>>() {}.getType());
        return ordersService.addOrder(userId, totalPrice, cartList, address);
    }

    @GetMapping("/detail")
    public Result<?> detail(Integer orderId, HttpServletRequest request) {
        if (orderId == null) {
            return Result.fail("参数错误");
        }

        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("未登录");
        }

        return ordersService.findOrderByOrderId(orderId, userId);
    }

    @GetMapping("/items")
    public Result<?> items(Integer orderId, HttpServletRequest request) {
        if (orderId == null) {
            return Result.fail("参数错误");
        }

        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("未登录");
        }

        return ordersService.findItemByOrderId(orderId, userId);
    }

    @PostMapping("/cancel")
    public Result<?> cancel(@RequestBody Map<String, Integer> params, HttpServletRequest request) {
        Integer orderId = params.get("orderId");
        if (orderId == null) {
            return Result.fail("参数错误");
        }

        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("未登录");
        }

        return ordersService.cancelOrder(orderId, userId);
    }

    /**
     * 批量取消订单
     */
    @PostMapping("/cancelBatch")
    public Result<?> cancelBatch(@RequestBody List<Integer> orderIds, HttpServletRequest request) {
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("未登录");
        }
        if (orderIds == null || orderIds.isEmpty()) {
            return Result.fail("订单ID不能为空");
        }
        return ordersService.cancelBatch(orderIds, userId);
    }

    @GetMapping("/list")
    public Result<?> list(HttpServletRequest request) {
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("未登录");
        }

        return ordersService.findOrdersByUserId(userId);
    }

    @PostMapping("/updateStatuses")
    public Result<?> updateOrderStatuses(@RequestBody List<Integer> orderIds, HttpServletRequest request) {
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("未登录");
        }

        if (orderIds == null || orderIds.isEmpty()) {
            return Result.fail("订单ID不能为空");
        }

        int updatedCount = 0;
        for (Integer orderId : orderIds) {
            try {
                Orders order = ordersMapper.selectById(orderId);
                if (order != null && userId.equals(order.getUserId()) && "fail".equals(order.getPaymentStatus())) {
                    AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
                    Map<String, Object> bizModel = new HashMap<>();
                    bizModel.put("out_trade_no", orderId.toString());
                    queryRequest.setBizContent(JSON.toJSONString(bizModel));

                    AlipayTradeQueryResponse response = alipayClient.execute(queryRequest);
                    if (response.isSuccess() && "TRADE_SUCCESS".equals(response.getTradeStatus())) {
                        order.setPaymentStatus("success");
                        ordersMapper.updateById(order);
                        updatedCount++;
                    }
                }
            } catch (AlipayApiException e) {
                // 查询失败，跳过该订单
            }
        }

        return Result.success("更新完成", updatedCount);
    }
}