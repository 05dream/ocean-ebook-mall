package com.edu.wikipro.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alibaba.fastjson.JSON;
import com.edu.wikipro.common.Result;
import com.edu.wikipro.config.AlipayConfig;
import com.edu.wikipro.entity.Orders;
import com.edu.wikipro.mapper.OrdersMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/alipay")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AlipayController {

    private static final Logger logger = LoggerFactory.getLogger(AlipayController.class);

    @Resource
    private AlipayConfig alipayConfig;

    @Resource
    private AlipayClient alipayClient;

    @Resource
    private OrdersMapper ordersMapper;

    @PostMapping("/pay")
    public Result<?> pay(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Integer orderId = params.get("orderId") != null ? ((Number) params.get("orderId")).intValue() : null;
        String subject = params.get("subject") != null ? params.get("subject").toString() : "订单支付";

        logger.info("支付宝支付请求: orderId={}, subject={}", orderId, subject);

        if (orderId == null) {
            logger.warn("订单号为空");
            return Result.fail("订单号不能为空");
        }

        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            logger.warn("订单不存在: orderId={}", orderId);
            return Result.fail("订单不存在");
        }

        logger.info("订单信息: orderId={}, totalPrice={}, paymentStatus={}", order.getOrderId(), order.getTotalPrice(), order.getPaymentStatus());

        try {
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            alipayRequest.setReturnUrl(alipayConfig.getReturnUrl());
            alipayRequest.setNotifyUrl(alipayConfig.getNotifyUrl());

            Map<String, Object> bizModel = new HashMap<>();
            bizModel.put("out_trade_no", orderId.toString());
            bizModel.put("total_amount", String.format("%.2f", order.getTotalPrice()));
            bizModel.put("subject", subject);
            bizModel.put("product_code", "FAST_INSTANT_TRADE_PAY");

            logger.info("支付宝请求参数: {}", JSON.toJSONString(bizModel));

            alipayRequest.setBizContent(JSON.toJSONString(bizModel));

            String form = alipayClient.pageExecute(alipayRequest).getBody();
            logger.info("支付宝返回表单长度: {}", form != null ? form.length() : 0);
            return Result.success("获取支付表单成功", form);
        } catch (AlipayApiException e) {
            return Result.fail("支付失败: " + e.getMessage());
        }
    }

    @GetMapping("/payPage")
    public String payPage(Integer orderId, String subject) {
        if (orderId == null) {
            return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>支付错误</title></head><body><div style='text-align:center;margin-top:100px;color:red;font-size:18px;'>订单号不能为空</div></body></html>";
        }

        Orders order = ordersMapper.selectById(orderId);
        if (order == null) {
            return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>支付错误</title></head><body><div style='text-align:center;margin-top:100px;color:red;font-size:18px;'>订单不存在</div></body></html>";
        }

        try {
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            alipayRequest.setReturnUrl(alipayConfig.getReturnUrl());
            alipayRequest.setNotifyUrl(alipayConfig.getNotifyUrl());

            Map<String, Object> bizModel = new HashMap<>();
            bizModel.put("out_trade_no", orderId.toString());
            bizModel.put("total_amount", String.format("%.2f", order.getTotalPrice()));
            bizModel.put("subject", subject != null ? subject : "订单支付");
            bizModel.put("product_code", "FAST_INSTANT_TRADE_PAY");

            alipayRequest.setBizContent(JSON.toJSONString(bizModel));

            String formHtml = alipayClient.pageExecute(alipayRequest).getBody();
            
            return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>正在跳转支付...</title><script>window.onload = function() { var forms = document.querySelectorAll('form'); if(forms.length > 0) { forms[0].submit(); } }</script></head><body>" + formHtml + "<div style='display:none;'>如果页面没有自动跳转，请点击下方按钮</div></body></html>";
        } catch (AlipayApiException e) {
            return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>支付失败</title></head><body><div style='text-align:center;margin-top:100px;color:red;font-size:18px;'>支付失败: " + e.getMessage() + "</div></body></html>";
        }
    }

    @PostMapping("/notify")
    public String notifyUrl(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            StringBuilder valueStr = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                valueStr.append((i == values.length - 1) ? values[i] : values[i] + ",");
            }
            params.put(name, valueStr.toString());
        }

        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.getPublicKey(),
                    "UTF-8", "RSA2");
            if (signVerified) {
                String orderId = params.get("out_trade_no");
                String tradeStatus = params.get("trade_status");

                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    Orders order = ordersMapper.selectById(Integer.parseInt(orderId));
                    if (order != null) {
                        order.setPaymentStatus("success");
                        ordersMapper.updateById(order);
                    }
                }
                return "success";
            } else {
                return "fail";
            }
        } catch (AlipayApiException e) {
            return "fail";
        }
    }

    @GetMapping("/return")
    public String returnUrl(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            StringBuilder valueStr = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                valueStr.append((i == values.length - 1) ? values[i] : values[i] + ",");
            }
            params.put(name, valueStr.toString());
        }

        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(params, alipayConfig.getPublicKey(),
                    "UTF-8", "RSA2");
            if (signVerified) {
                String orderId = params.get("out_trade_no");
                String tradeStatus = params.get("trade_status");

                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    Orders order = ordersMapper.selectById(Integer.parseInt(orderId));
                    if (order != null) {
                        order.setPaymentStatus("success");
                        ordersMapper.updateById(order);
                    }
                }
                return "<html><body><h1>支付成功！</h1><p>订单号：" + orderId + "</p><p>支付状态：" + tradeStatus + "</p><p>正在返回商城...</p><script>setTimeout(function(){window.close();}, 3000);</script></body></html>";
            } else {
                return "<html><body><h1>支付失败：签名验证失败</h1></body></html>";
            }
        } catch (AlipayApiException e) {
            return "<html><body><h1>支付失败：" + e.getMessage() + "</h1></body></html>";
        }
    }

    @PostMapping("/updateStatuses")
    public Result<?> updateOrderStatuses(@RequestBody List<Integer> orderIds, HttpServletRequest request) {
        if (orderIds == null || orderIds.isEmpty()) {
            return Result.fail("订单ID不能为空");
        }

        int updatedCount = 0;
        for (Integer orderId : orderIds) {
            try {
                Orders order = ordersMapper.selectById(orderId);
                if (order != null && "fail".equals(order.getPaymentStatus())) {
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