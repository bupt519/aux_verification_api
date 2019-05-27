package cn.edu.bupt.util;

public enum ResultTypeEnum {

    SERVICE_SUCCESS(200,"OK"),
    PARAM_ERROR(400,"Bad Request"),
    SERVICE_ERROR(500,"Internal Server Error"),
    UNAUTHORIZED_ERROR(401, "Unauthorized"),
    NON_AUTHORITATIVE_INFORMATION_ERROR(203, "Non-Authoritative Information");

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
