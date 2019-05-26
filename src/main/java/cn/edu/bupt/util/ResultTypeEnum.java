package cn.edu.bupt.util;

public enum ResultTypeEnum {

    SERVICE_SUCCESS(200,"成功"),
    PARAM_ERROR(400,"入参异常"),
    SERVICE_ERROR(500,"服务异常");

    private Integer code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    ResultTypeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
