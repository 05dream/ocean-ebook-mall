package com.edu.wikipro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.wikipro.common.Result;
import com.edu.wikipro.entity.WxUser;

public interface WxUserService extends IService<WxUser> {
    Result<String> register(WxUser wxUser);
    Result<String> login(WxUser wxUser);
    Result<WxUser> getUserInfo(String token);
    Result<String> updateUserInfo(String token, WxUser wxUser);
    void updateUserImg(String username, String userImg);
}
