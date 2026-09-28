package com.edu.wikipro;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 扫描所有Mapper接口
@MapperScan("com.edu.wikipro.mapper")
public class WikiproApplication {
    public static void main(String[] args) {
        SpringApplication.run(WikiproApplication.class, args);
    }
}