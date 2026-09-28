CREATE DATABASE IF NOT EXISTS shopdb DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shopdb;

DROP TABLE IF EXISTS `order_item`;
DROP TABLE IF EXISTS `order`;
DROP TABLE IF EXISTS `cart`;
DROP TABLE IF EXISTS `collection`;
DROP TABLE IF EXISTS `wx_banner`;
DROP TABLE IF EXISTS `doc`;
DROP TABLE IF EXISTS `wx_user`;

CREATE TABLE `wx_user` (
    `user_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `user_img` VARCHAR(255) DEFAULT NULL COMMENT '用户头像',
    `flag` VARCHAR(20) DEFAULT '普通用户' COMMENT '用户标识',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别',
    `birthday` VARCHAR(20) DEFAULT NULL COMMENT '生日',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '地址'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE `doc` (
    `doc_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '商品ID',
    `doc_title` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `doc_desc` TEXT DEFAULT NULL COMMENT '商品描述',
    `image` VARCHAR(255) DEFAULT NULL COMMENT '商品图片',
    `author` VARCHAR(100) DEFAULT NULL COMMENT '作者',
    `views` INT DEFAULT 0 COMMENT '浏览量',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '分类',
    `stock` INT DEFAULT 0 COMMENT '库存',
    `sales` INT DEFAULT 0 COMMENT '销量',
    `price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '价格'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

CREATE TABLE `cart` (
    `cart_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '购物车ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `goods_id` INT NOT NULL COMMENT '商品ID',
    `quantity` INT DEFAULT 1 COMMENT '数量',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_goods_id` (`goods_id`),
    FOREIGN KEY (`user_id`) REFERENCES `wx_user`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`goods_id`) REFERENCES `doc`(`doc_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

CREATE TABLE `order` (
    `order_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `total_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '总价',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `payment_status` VARCHAR(20) DEFAULT 'fail' COMMENT '支付状态(fail/success)',
    INDEX `idx_user_id` (`user_id`),
    FOREIGN KEY (`user_id`) REFERENCES `wx_user`(`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

CREATE TABLE `order_item` (
    `item_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '订单项ID',
    `order_id` INT NOT NULL COMMENT '订单ID',
    `goods_id` INT NOT NULL COMMENT '商品ID',
    `quantity` INT DEFAULT 1 COMMENT '数量',
    `price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '单价',
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_goods_id` (`goods_id`),
    FOREIGN KEY (`order_id`) REFERENCES `order`(`order_id`) ON DELETE CASCADE,
    FOREIGN KEY (`goods_id`) REFERENCES `doc`(`doc_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项表';

CREATE TABLE `wx_banner` (
    `banner_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '轮播图ID',
    `imgurl` VARCHAR(255) NOT NULL COMMENT '图片URL'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';

CREATE TABLE `collection` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    `user_id` INT NOT NULL COMMENT '用户ID',
    `doc_id` INT NOT NULL COMMENT '商品ID',
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_doc_id` (`doc_id`),
    FOREIGN KEY (`user_id`) REFERENCES `wx_user`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`doc_id`) REFERENCES `doc`(`doc_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';