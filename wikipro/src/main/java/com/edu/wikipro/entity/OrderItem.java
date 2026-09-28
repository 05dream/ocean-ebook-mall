package com.edu.wikipro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("order_item")
public class OrderItem implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer itemId;
    private Integer orderId;
    private Integer goodsId;
    private Integer quantity;
    private Double price;
}