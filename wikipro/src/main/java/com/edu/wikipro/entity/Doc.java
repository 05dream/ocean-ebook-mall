package com.edu.wikipro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("doc")
public class Doc implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer docId;
    private String docTitle;
    private String docDesc;
    private String image;
    private String author;
    private Integer views;
    private String category;
    private Integer stock;
    private Integer sales;
    private Double price;
}
