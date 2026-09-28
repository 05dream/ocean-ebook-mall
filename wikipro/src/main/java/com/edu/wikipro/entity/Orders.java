package com.edu.wikipro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@TableName("`order`")
public class Orders implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer orderId;
    private Integer userId;
    private Double totalPrice;
    private Timestamp createTime;
    private String paymentStatus;
    private String address;
}