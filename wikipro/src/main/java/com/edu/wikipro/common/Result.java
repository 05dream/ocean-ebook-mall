package com.edu.wikipro.common;
import lombok.Data;
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
    // 成功
    public static <T> Result<T> success(String msg, T data){
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }
    // 无数据成功
    public static <T> Result<T> success(String msg){
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMsg(msg);
        return r;
    }
    // 失败
    public static <T> Result<T> fail(String msg){
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }
}