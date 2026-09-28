package com.edu.wikipro.controller;

import com.edu.wikipro.common.Result;
import com.edu.wikipro.entity.WxUser;
import com.edu.wikipro.service.WxUserService;
import com.edu.wikipro.utils.JWTUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WxUserController {
    @Resource
    private WxUserService wxUserService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody WxUser wxUser){
        return wxUserService.register(wxUser);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody WxUser wxUser){
        return wxUserService.login(wxUser);
    }

    @GetMapping("/info")
    public Result<WxUser> getUserInfo(@RequestHeader(value = "Authorization", required = false) String token){
        return wxUserService.getUserInfo(token);
    }

    @PostMapping("/update")
    public Result<String> updateUserInfo(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody WxUser wxUser){
        return wxUserService.updateUserInfo(token, wxUser);
    }

    /**
     * 上传头像
     */
    @PostMapping("/uploadAvatar")
    public Result<String> uploadAvatar(@RequestHeader(value = "Authorization", required = false) String token,
                                       @RequestParam("file") MultipartFile file) {
        if (token == null || token.isEmpty()) {
            return Result.fail("未登录");
        }
        String tokenStr = token.replace("Bearer ", "").trim();
        if (!JWTUtils.verifyToken(tokenStr)) {
            return Result.fail("Token无效");
        }
        if (file == null || file.isEmpty()) {
            return Result.fail("请选择图片");
        }

        // 限制文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.fail("只能上传图片");
        }

        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

            // 保存到 uploads/avatar 目录
            String dirPath = System.getProperty("user.dir") + "/uploads/avatar/";
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File dest = new File(dirPath + fileName);
            file.transferTo(dest);

            // 返回可访问的 URL
            String avatarUrl = "/uploads/avatar/" + fileName;

            // 更新用户的 userImg 字段
            String username = JWTUtils.getUsername(tokenStr);
            wxUserService.updateUserImg(username, avatarUrl);

            return Result.success("上传成功", avatarUrl);
        } catch (IOException e) {
            return Result.fail("上传失败: " + e.getMessage());
        }
    }
}
