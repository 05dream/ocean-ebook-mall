package com.edu.wikipro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.wikipro.common.Result;
import com.edu.wikipro.entity.Cart;
import com.edu.wikipro.entity.Doc;
import com.edu.wikipro.entity.vo.CartGoodsVO;
import com.edu.wikipro.mapper.CartMapper;
import com.edu.wikipro.mapper.DocMapper;
import com.edu.wikipro.service.CartService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    @Resource
    private DocMapper docMapper;

    @Override
    public Result<?> addCart(Integer userId, Integer goodsId, Integer quantity) {
        try {
            Doc doc = docMapper.selectById(goodsId);
            if (doc == null) {
                return Result.fail("商品不存在");
            }

            LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Cart::getUserId, userId).eq(Cart::getGoodsId, goodsId);
            Cart cart = baseMapper.selectOne(wrapper);

            if (cart != null) {
                cart.setQuantity(cart.getQuantity() + quantity);
                baseMapper.updateById(cart);
                return Result.success("数量已更新");
            }

            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setGoodsId(goodsId);
            newCart.setQuantity(quantity);
            newCart.setCreateTime(new Timestamp(System.currentTimeMillis()));
            baseMapper.insert(newCart);
            return Result.success("添加购物车成功");
        } catch (Exception e) {
            return Result.fail("服务器错误");
        }
    }

    @Override
    public Result<?> findCartByUserId(Integer userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        List<Cart> carts = baseMapper.selectList(wrapper);

        List<CartGoodsVO> result = new ArrayList<>();
        for (Cart cart : carts) {
            Doc doc = docMapper.selectById(cart.getGoodsId());
            if (doc != null) {
                CartGoodsVO vo = new CartGoodsVO();
                vo.setId(doc.getDocId());
                vo.setCartId(cart.getCartId());
                vo.setName(doc.getDocTitle());
                vo.setImage(doc.getImage());
                vo.setQuantity(cart.getQuantity());
                vo.setCategory(doc.getCategory());
                vo.setPrice(doc.getPrice());
                result.add(vo);
            }
        }
        return Result.success("获取成功", result);
    }

    @Override
    public Result<?> updateQuantity(Integer cartId, Integer quantity) {
        Cart cart = baseMapper.selectById(cartId);
        if (cart == null) {
            return Result.fail("购物车记录不存在");
        }
        cart.setQuantity(quantity);
        baseMapper.updateById(cart);
        return Result.success("更新成功");
    }

    @Override
    public Result<?> deleteCart(Integer cartId) {
        Cart cart = baseMapper.selectById(cartId);
        if (cart == null) {
            return Result.fail("购物车记录不存在");
        }
        baseMapper.deleteById(cartId);
        return Result.success("删除成功");
    }

    @Override
    public Result<?> batchDeleteCart(List<Integer> cartIds) {
        if (cartIds == null || cartIds.isEmpty()) {
            return Result.fail("参数错误");
        }
        baseMapper.deleteBatchIds(cartIds);
        return Result.success("批量删除成功");
    }

    @Override
    public Result<?> getCartCount(Integer userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        Integer count = Math.toIntExact(baseMapper.selectCount(wrapper));
        return Result.success("获取成功", count);
    }
}