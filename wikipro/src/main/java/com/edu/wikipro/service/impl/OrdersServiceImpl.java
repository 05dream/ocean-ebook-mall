package com.edu.wikipro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.wikipro.common.Result;
import com.edu.wikipro.entity.OrderItem;
import com.edu.wikipro.entity.Orders;
import com.edu.wikipro.entity.Doc;
import com.edu.wikipro.mapper.OrderItemMapper;
import com.edu.wikipro.mapper.OrdersMapper;
import com.edu.wikipro.mapper.DocMapper;
import com.edu.wikipro.service.OrdersService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private DocMapper docMapper;

    @Resource
    private com.edu.wikipro.mapper.CartMapper cartMapper;

    @Override
    @Transactional
    public Result<?> addOrder(Integer userId, Double totalPrice, List<Map<String, Object>> cartList, String address) {
        try {
            Orders order = new Orders();
            order.setUserId(userId);
            order.setTotalPrice(totalPrice);
            order.setCreateTime(new Timestamp(System.currentTimeMillis()));
            order.setPaymentStatus("fail");
            order.setAddress(address);
            baseMapper.insert(order);

            Integer orderId = order.getOrderId();

            for (Map<String, Object> item : cartList) {
                Integer goodsId = ((Number) item.get("goodsId")).intValue();
                Integer quantity = ((Number) item.get("quantity")).intValue();

                Doc doc = docMapper.selectById(goodsId);
                if (doc == null) {
                    throw new RuntimeException("商品不存在");
                }
                if (doc.getStock() == null || doc.getStock() < quantity) {
                    throw new RuntimeException("库存不足");
                }

                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(orderId);
                orderItem.setGoodsId(goodsId);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(doc.getPrice() != null ? doc.getPrice() : 0.0);
                orderItemMapper.insert(orderItem);

                doc.setStock(doc.getStock() - quantity);
                doc.setSales(doc.getSales() != null ? doc.getSales() + quantity : quantity);
                docMapper.updateById(doc);

                LambdaQueryWrapper<com.edu.wikipro.entity.Cart> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(com.edu.wikipro.entity.Cart::getUserId, userId).eq(com.edu.wikipro.entity.Cart::getGoodsId, goodsId);
                cartMapper.delete(wrapper);
            }

            return Result.success("下单成功", orderId);
        } catch (Exception e) {
            throw new RuntimeException("下单失败: " + e.getMessage());
        }
    }

    @Override
    public Result<?> findOrderByOrderId(Integer orderId, Integer userId) {
        Orders order = baseMapper.selectById(orderId);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.fail("无权查看该订单");
        }
        return Result.success("获取成功", order);
    }

    @Override
    public Result<?> findItemByOrderId(Integer orderId, Integer userId) {
        Orders order = baseMapper.selectById(orderId);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.fail("无权查看该订单");
        }
        
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(wrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (OrderItem item : items) {
            Doc doc = docMapper.selectById(item.getGoodsId());
            if (doc != null) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("itemId", item.getItemId());
                itemMap.put("goodsId", item.getGoodsId());
                itemMap.put("name", doc.getDocTitle());
                itemMap.put("image", doc.getImage());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("price", item.getPrice());
                result.add(itemMap);
            }
        }
        return Result.success("获取成功", result);
    }

    @Override
    @Transactional
    public Result<?> cancelOrder(Integer orderId, Integer userId) {
        Orders order = baseMapper.selectById(orderId);
        if (order == null) {
            return Result.fail("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.fail("无权取消该订单");
        }
        if ("success".equals(order.getPaymentStatus())) {
            return Result.fail("已支付订单无法取消");
        }

        // 归还库存
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(wrapper);
        for (OrderItem item : items) {
            Doc doc = docMapper.selectById(item.getGoodsId());
            if (doc != null) {
                doc.setStock(doc.getStock() != null ? doc.getStock() + item.getQuantity() : item.getQuantity());
                doc.setSales(doc.getSales() != null ? Math.max(0, doc.getSales() - item.getQuantity()) : 0);
                docMapper.updateById(doc);
            }
        }

        orderItemMapper.delete(wrapper);
        baseMapper.deleteById(orderId);
        return Result.success("取消成功");
    }

    @Override
    @Transactional
    public Result<?> cancelBatch(List<Integer> orderIds, Integer userId) {
        if (orderIds == null || orderIds.isEmpty()) {
            return Result.fail("订单ID不能为空");
        }
        int successCount = 0;
        int failCount = 0;
        for (Integer orderId : orderIds) {
            try {
                Orders order = baseMapper.selectById(orderId);
                if (order == null || !order.getUserId().equals(userId)) {
                    failCount++;
                    continue;
                }
                if ("success".equals(order.getPaymentStatus())) {
                    failCount++;
                    continue;
                }
                // 归还库存
                LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(OrderItem::getOrderId, orderId);
                List<OrderItem> items = orderItemMapper.selectList(wrapper);
                for (OrderItem item : items) {
                    Doc doc = docMapper.selectById(item.getGoodsId());
                    if (doc != null) {
                        doc.setStock(doc.getStock() != null ? doc.getStock() + item.getQuantity() : item.getQuantity());
                        doc.setSales(doc.getSales() != null ? Math.max(0, doc.getSales() - item.getQuantity()) : 0);
                        docMapper.updateById(doc);
                    }
                }
                orderItemMapper.delete(wrapper);
                baseMapper.deleteById(orderId);
                successCount++;
            } catch (Exception e) {
                failCount++;
            }
        }
        return Result.success("成功取消" + successCount + "个订单，失败" + failCount + "个", successCount);
    }

    @Override
    public Result<?> findOrdersByUserId(Integer userId) {
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Orders::getUserId, userId).orderByDesc(Orders::getCreateTime);
        List<Orders> orders = baseMapper.selectList(wrapper);
        return Result.success("获取成功", orders);
    }
}