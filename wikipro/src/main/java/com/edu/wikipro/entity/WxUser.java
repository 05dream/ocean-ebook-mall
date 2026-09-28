package com.edu.wikipro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("wx_user")
public class WxUser implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer userId;
    private String username;
    private String password;
    private String userImg;
    private String flag;
    private String realName;
    private String gender;
    private String birthday;
    private String phone;
    private String email;
    private String address;
}
