package com.edu.wikipro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@TableName("cart")
public class Cart implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer cartId;
    private Integer userId;
    private Integer goodsId;
    private Integer quantity;
    private Timestamp createTime;
    
    @TableField(exist = false)
    private WxUser user;
    
    @TableField(exist = false)
    private Doc goods;
}