package com.edu.wikipro.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;

@Data
@TableName("wx_banner")
public class Banner implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer bannerId;
    private String imgurl;
}
