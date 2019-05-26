package cn.edu.bupt.util.token;

import lombok.Data;

@Data
public class Identity {

    private String token;
    private Long id; // 对应user_id
    private String issuer;
    private String clientId; //可以是Oauth2.0中的client_id，也可以是一般的username
    private Long duration; // 有效时长，单位毫秒

}
