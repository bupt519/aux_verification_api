package cn.edu.bupt.bean.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Identity {

    private String token;
    private Long id; // 对应user_id
    private String issuer;
    private String clientId; //可以是Oauth2.0中的client_id，也可以是一般的username
    private Long duration; // 有效时长，单位毫秒
    private boolean isLogin;

}
