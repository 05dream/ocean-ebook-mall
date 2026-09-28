package com.edu.wikipro.entity.vo;

import lombok.Data;

@Data
public class CartGoodsVO {
    private Integer id;
    private Integer cartId;
    private String name;
    private String image;
    private Integer quantity;
    private String category;
    private Double price;
}