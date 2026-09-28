package com.edu.wikipro.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.edu.wikipro.common.Result;
import com.edu.wikipro.entity.WxUser;
import com.edu.wikipro.mapper.WxUserMapper;
import com.edu.wikipro.service.DocCollectionService;
import com.edu.wikipro.utils.JWTUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/collection")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CollectionController {

    @Resource
    private DocCollectionService collectionService;
    
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
        Integer docId = params.get("docId");
        if (docId == null) {
            return Result.fail("参数错误");
        }
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("未登录");
        }
        return collectionService.addCollection(userId, docId);
    }

    @PostMapping("/remove")
    public Result<?> remove(@RequestBody java.util.Map<String, Integer> params, HttpServletRequest request) {
        Integer docId = params.get("docId");
        if (docId == null) {
            return Result.fail("参数错误");
        }
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("未登录");
        }
        return collectionService.removeCollection(userId, docId);
    }

    @GetMapping("/check")
    public Result<?> check(Integer docId, HttpServletRequest request) {
        if (docId == null) {
            return Result.fail("参数错误");
        }
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("未登录");
        }
        return collectionService.checkCollection(userId, docId);
    }

    @GetMapping("/list")
    public Result<?> list(HttpServletRequest request) {
        Integer userId = getUserIdFromToken(request);
        if (userId == null) {
            return Result.fail("未登录");
        }
        return collectionService.getCollectionsByUserId(userId);
    }
}