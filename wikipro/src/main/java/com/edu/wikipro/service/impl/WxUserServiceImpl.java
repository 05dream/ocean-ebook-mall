package com.edu.wikipro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.wikipro.common.Result;
import com.edu.wikipro.entity.WxUser;
import com.edu.wikipro.mapper.WxUserMapper;
import com.edu.wikipro.service.WxUserService;
import com.edu.wikipro.utils.JWTUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class WxUserServiceImpl extends ServiceImpl<WxUserMapper, WxUser> implements WxUserService {
    
    private static final Logger logger = LoggerFactory.getLogger(WxUserServiceImpl.class);
    
    @Resource
    private WxUserMapper wxUserMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Result<String> register(WxUser wxUser) {
        logger.info("开始注册用户: username={}", wxUser.getUsername());
        
        QueryWrapper<WxUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", wxUser.getUsername());
        WxUser exist = wxUserMapper.selectOne(wrapper);
        
        if(exist != null){
            logger.warn("用户名已存在: {}", wxUser.getUsername());
            return Result.fail("用户名已存在");
        }
        
        String encodePwd = passwordEncoder.encode(wxUser.getPassword());
        logger.info("密码加密完成, 加密前: {}, 加密后长度: {}", wxUser.getPassword(), encodePwd.length());
        wxUser.setPassword(encodePwd);
        
        int insert = wxUserMapper.insert(wxUser);
        logger.info("插入结果: insert={}", insert);
        
        if(insert > 0){
            logger.info("注册成功: {}", wxUser.getUsername());
            return Result.success("注册成功");
        }
        logger.error("注册失败: {}", wxUser.getUsername());
        return Result.fail("注册失败");
    }

    @Override
    public Result<String> login(WxUser wxUser) {
        logger.info("开始登录: username={}", wxUser.getUsername());
        
        try {
            QueryWrapper<WxUser> wrapper = new QueryWrapper<>();
            wrapper.eq("username", wxUser.getUsername());
            WxUser userDB = wxUserMapper.selectOne(wrapper);
            
            if(userDB == null){
                logger.warn("用户不存在: {}", wxUser.getUsername());
                return Result.fail("用户名或密码错误");
            }
            
            logger.info("用户存在, 数据库密码长度: {}", userDB.getPassword().length());
            boolean matches = passwordEncoder.matches(wxUser.getPassword(), userDB.getPassword());
            logger.info("密码比对结果: {}", matches);
            
            if(!matches){
                logger.warn("密码不匹配: {}", wxUser.getUsername());
                return Result.fail("用户名或密码错误");
            }
            
            String token = JWTUtils.createToken(userDB.getUsername());
            logger.info("登录成功: {}, token={}", userDB.getUsername(), token);
            return Result.success("登录成功", token);
        } catch (Exception e) {
            logger.error("登录异常: {}", e.getMessage(), e);
            return Result.fail("登录异常: " + e.getMessage());
        }
    }

    @Override
    public Result<WxUser> getUserInfo(String token) {
        logger.info("获取用户信息: token={}", token);
        
        if(token == null || token.isEmpty()){
            return Result.fail("未登录");
        }
        
        String tokenStr = token.replace("Bearer ", "").trim();
        
        if(!JWTUtils.verifyToken(tokenStr)){
            logger.warn("Token无效");
            return Result.fail("Token无效");
        }
        
        String username = JWTUtils.getUsername(tokenStr);
        logger.info("从Token解析用户名: {}", username);
        
        QueryWrapper<WxUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        WxUser user = wxUserMapper.selectOne(wrapper);
        
        if(user == null){
            logger.warn("用户不存在: {}", username);
            return Result.fail("用户不存在");
        }
        
        user.setPassword(null);
        
        logger.info("获取用户信息成功: {}", username);
        return Result.success("获取成功", user);
    }

    @Override
    @Transactional
    public Result<String> updateUserInfo(String token, WxUser wxUser) {
        logger.info("更新用户信息");
        
        if(token == null || token.isEmpty()){
            return Result.fail("未登录");
        }
        
        String tokenStr = token.replace("Bearer ", "").trim();
        
        if(!JWTUtils.verifyToken(tokenStr)){
            logger.warn("Token无效");
            return Result.fail("Token无效");
        }
        
        String username = JWTUtils.getUsername(tokenStr);
        
        QueryWrapper<WxUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        WxUser user = wxUserMapper.selectOne(wrapper);
        
        if(user == null){
            return Result.fail("用户不存在");
        }
        
        if(wxUser.getRealName() != null){
            user.setRealName(wxUser.getRealName());
        }
        if(wxUser.getGender() != null){
            user.setGender(wxUser.getGender());
        }
        if(wxUser.getBirthday() != null){
            user.setBirthday(wxUser.getBirthday());
        }
        if(wxUser.getPhone() != null){
            user.setPhone(wxUser.getPhone());
        }
        if(wxUser.getEmail() != null){
            user.setEmail(wxUser.getEmail());
        }
        if(wxUser.getAddress() != null){
            user.setAddress(wxUser.getAddress());
        }
        if(wxUser.getUserImg() != null){
            user.setUserImg(wxUser.getUserImg());
        }
        
        int update = wxUserMapper.updateById(user);
        
        if(update > 0){
            logger.info("更新用户信息成功: {}", username);
            return Result.success("更新成功");
        }
        return Result.fail("更新失败");
    }

    @Override
    public void updateUserImg(String username, String userImg) {
        QueryWrapper<WxUser> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        WxUser user = wxUserMapper.selectOne(wrapper);
        if (user != null) {
            user.setUserImg(userImg);
            wxUserMapper.updateById(user);
        }
    }
}
