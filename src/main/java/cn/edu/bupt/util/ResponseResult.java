package cn.edu.bupt.util;

import java.io.Serializable;

public class ResponseResult<T> implements Serializable {

    private String message;
    private T result;

    private ResponseResult() {
    }

    private ResponseResult(String message){
        this.message = message;
    }

    private ResponseResult(String message, T result){
        this.message = message;
        this.result = result;
    }

    public static <T> ResponseResult<T> of(String message){
        return new ResponseResult<>(message);
    }

    public static <T> ResponseResult<T> of(String message, T result){
        return new ResponseResult<>(message, result);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
