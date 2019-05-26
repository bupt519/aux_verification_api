package cn.edu.bupt.util;

import java.io.Serializable;

public class ResponseResult<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    private ResponseResult() {
    }

    public ResponseResult(ResultTypeEnum type) {
        this.code = type.getCode();
        this.msg = type.getMessage();
    }

    public ResponseResult(ResultTypeEnum type, T data) {
        this.code = type.getCode();
        this.msg = type.getMessage();
        this.data = data;
    }

    public ResponseResult(ResultTypeEnum type, String content, T data) {
        this.code = type.getCode();
        this.msg = content;
        this.data = data;
    }

    public static ResponseResult success() {
        return new ResponseResult(ResultTypeEnum.SERVICE_SUCCESS);
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult(ResultTypeEnum.SERVICE_SUCCESS, data);
    }

    public static <T> ResponseResult<T> error(T data) {
        return new ResponseResult(ResultTypeEnum.SERVICE_ERROR, data);
    }

//    public static <T> ResponseResult<T> error(String content, T data){
//        return new ResponseResult<>(ResultTypeEnum.PARAM_ERROR, content, data);
//    }

    public static  <T> ResponseResult<T> success(String content, T data) {
        return new ResponseResult(ResultTypeEnum.SERVICE_SUCCESS, content, data);
    }

    public static <T> ResponseResult<T> error() {
        return new ResponseResult(ResultTypeEnum.SERVICE_ERROR);
    }

    public static <T> ResponseResult<T> error(ResultTypeEnum typeEnum) {
        return new ResponseResult(typeEnum);
    }

    public static <T> ResponseResult<T> error(ResultTypeEnum typeEnum, String msg) {
        return new ResponseResult(typeEnum,msg);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
